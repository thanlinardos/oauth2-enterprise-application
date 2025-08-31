package com.thanlinardos.cloud_config_server.vault;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.thanlinardos.cloud_config_server.batch.BatchJobProcessor;
import com.thanlinardos.cloud_config_server.vault.properties.batch.VaultSyncJobConfig;
import com.thanlinardos.cloud_config_server.vault.properties.update_credentials.ApplicationEnvironmentProperties;
import com.thanlinardos.cloud_config_server.vault.properties.update_credentials.ApplicationVaultProperties;
import com.thanlinardos.cloud_config_server.vault.properties.update_credentials.VaultUpdateCredentialsProperties;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.thanlinardos.cloud_config_server.vault.VaultIntegrationConstants.STR_SLASH_STR_FORMAT;
import static com.thanlinardos.cloud_config_server.vault.VaultIntegrationConstants.X_VAULT_TOKEN_HEADER;

@Slf4j
public class VaultSyncJob extends BatchJobProcessor<VaultSyncJobConfig> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public VaultSyncJob(VaultSyncJobConfig config) {
        super(config);
    }

    /**
     * The execution logic of the VaultSyncJob which performs the following steps:
     * <p>
     * 1. Fetches the {@link VaultUpdateCredentialsProperties} configuration data from Vault for the config server application.
     * <p>
     * 2. Marks any scheduled tasks for removal if their application-environment is no longer present in the fetched properties.
     * <p>
     * 3. Cancels any tasks that were marked for removal.
     * <p>
     * 4. Iterates over each application defined in the properties and synchronizes their database credentials for the selected environments.
     * <p>
     * For each application-environment, if synchronization is not already scheduled, it initiates the synchronization process.
     * The synchronization process fetches new database credentials from Vault and updates the corresponding KV store entry.
     * It also schedules the next synchronization before the lease expiry of the credentials.
     *
     * @return the interval in seconds for the next configuration update, depending on the {@link VaultUpdateCredentialsProperties#updateConfigsInterval()} property.
     */
    @Override
    protected long execute() {
        JsonNode kvData = getVaultAppKvData(config.getConfigServerName(), null);
        VaultUpdateCredentialsProperties properties = objectMapper.convertValue(kvData, VaultUpdateCredentialsProperties.class);
        markTasksForRemoval(properties);
        cancelMarkedTasks();

        properties.applications()
                .forEach(application -> syncDatabaseCredentialsForApplication(application, properties));
        return properties.updateConfigsInterval();
    }

    private void markTasksForRemoval(VaultUpdateCredentialsProperties properties) {
        scheduledTasks.values().stream()
                .filter(task -> isApplicationEnvironmentNotInProperties(task.getName(), properties))
                .forEach(task -> task.setMarkedForCancellation(true));
    }

    private JsonNode getVaultAppKvData(String appName, @Nullable String environment) {
        var kvDataResponse = makeVaultGetRequest(buildAppConfigPath(appName, environment));
        Object bodyData = Objects.requireNonNull(kvDataResponse.getBody()).get("data");
        JsonNode kvData = objectMapper.valueToTree(bodyData).get("data");
        Objects.requireNonNull(kvData);
        return kvData;
    }

    private JsonNode readNewCredentialsFromResponse(JsonNode body) {
        JsonNode credentials = body.get("data");
        String username = Objects.requireNonNull(credentials.get("username")).asText();
        String password = Objects.requireNonNull(credentials.get("password")).asText();
        Map<String, Map<String, String>> datasourceMap = Map.of("datasource", Map.of("username", username, "password", password));
        return objectMapper.valueToTree(datasourceMap);
    }

    private ResponseEntity<Map<String, Object>> makeVaultGetRequest(String path) {
        HttpHeaders headers = setupVaultHttpHeaders(false);
        var entity = new HttpEntity<>(headers);
        return config.getRestTemplate().exchange(buildVaultUrlPath(path), HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });
    }

    private String buildVaultUrlPath(String path) {
        return String.format(STR_SLASH_STR_FORMAT, config.getVaultUrl(), path);
    }

    private void updateVaultAppConfiguration(String appName, String environment, JsonNode data) throws JsonProcessingException {
        HttpHeaders headers = setupVaultHttpHeaders(true);
        var requestBody = Map.of("data", objectMapper.treeToValue(data, Map.class));
        var entity = new HttpEntity<>(requestBody, headers);
        config.getRestTemplate().exchange(buildVaultUrlPath(buildAppConfigPath(appName, environment)), HttpMethod.POST, entity, Map.class);
    }

    private String buildAppConfigPath(String appName, @Nullable String environment) {
        return Optional.ofNullable(environment)
                .map(env -> String.format("%s/%s/%s", config.getKvDataPath(), appName, env))
                .orElse(String.format(STR_SLASH_STR_FORMAT, config.getKvDataPath(), appName));
    }

    private HttpHeaders setupVaultHttpHeaders(boolean modifying) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_VAULT_TOKEN_HEADER, config.getToken());
        if (modifying) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        return headers;
    }

    private boolean isApplicationEnvironmentNotInProperties(String name, VaultUpdateCredentialsProperties properties) {
        return properties.applications().stream()
                .noneMatch(application -> hasApplicationEnvironment(name, application));
    }

    private boolean hasApplicationEnvironment(String name, ApplicationVaultProperties application) {
        return application.environments().stream()
                .anyMatch(environment -> getAppEnvName(application.name(), environment.name()).equals(name));
    }

    private void syncDatabaseCredentialsForApplication(ApplicationVaultProperties application, VaultUpdateCredentialsProperties properties) {
        application.environments().stream()
                .filter(environment -> isEnvironmentSelected(environment, properties))
                .filter(environment -> isSyncNotScheduledForEnvironment(application, environment))
                .forEach(environment -> syncDbCredentialsForAppEnvWithRetry(application.name(), environment));
    }

    private boolean isSyncNotScheduledForEnvironment(ApplicationVaultProperties application, ApplicationEnvironmentProperties environment) {
        String taskName = getAppEnvName(application.name(), environment.name());
        return isTaskNotScheduled(taskName);
    }

    private String getAppEnvName(String application, String environment) {
        return String.format(STR_SLASH_STR_FORMAT, application, environment);
    }

    private boolean isEnvironmentSelected(ApplicationEnvironmentProperties environment, VaultUpdateCredentialsProperties properties) {
        return properties.syncAllEnvironments() || properties.environmentsToSync().contains(environment.name());
    }

    private void syncDbCredentialsForAppEnvWithRetry(String appName, ApplicationEnvironmentProperties environment) {
        try {
            syncDbCredentialsForAppEnv(appName, environment);
        } catch (Exception e) {
            logErrorAndRetryFailedTask(e, getAppEnvName(appName, environment.name()), () -> syncDbCredentialsForAppEnvWithRetry(appName, environment));
        }
    }

    private void syncDbCredentialsForAppEnv(String appName, ApplicationEnvironmentProperties environment) throws JsonPatchException, JsonProcessingException {
        var response = makeVaultGetRequest("database/creds/" + environment.role());
        var body = objectMapper.valueToTree(Objects.requireNonNull(response.getBody()));
        JsonNode newDataSourceCredentials = readNewCredentialsFromResponse(body);
        int leaseDuration = Objects.requireNonNull(body.get("lease_duration")).asInt();

        // Store credentials in KV store secret for the specified application-environment, after merging with existing data
        JsonNode kvData = getVaultAppKvData(appName, environment.name());
        JsonNode mergedKvData = JsonMergePatch.fromJson(newDataSourceCredentials).apply(kvData);
        updateVaultAppConfiguration(appName, environment.name(), mergedKvData);
        log.info("[{}/{}] Updated Vault KV with new database credentials.", appName, environment.name());

        // Dynamically schedule the next update before lease expiry (90% of lease duration)
        long refreshTime = (long) (leaseDuration * (config.getMaxLeaseExpiryPercent() / 100.0));
        log.info("[{}/{}] Next credential refresh scheduled in {} seconds.", appName, environment.name(), refreshTime);
        rescheduleTask(refreshTime, () -> syncDbCredentialsForAppEnvWithRetry(appName, environment), getAppEnvName(appName, environment.name()));
    }
}

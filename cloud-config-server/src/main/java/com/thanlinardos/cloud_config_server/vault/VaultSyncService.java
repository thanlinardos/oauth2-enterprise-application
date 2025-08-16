package com.thanlinardos.cloud_config_server.vault;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.thanlinardos.cloud_config_server.vault.properties.ApplicationEnvironmentProperties;
import com.thanlinardos.cloud_config_server.vault.properties.ApplicationVaultProperties;
import com.thanlinardos.cloud_config_server.vault.properties.VaultUpdateCredentialsProperties;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

import static com.thanlinardos.cloud_config_server.vault.VaultIntegrationConstants.STR_SLASH_STR_FORMAT;
import static com.thanlinardos.cloud_config_server.vault.VaultIntegrationConstants.X_VAULT_TOKEN_HEADER;

@Slf4j
public class VaultSyncService {

    private final String configServerName;
    private final String kvDataPath;
    private final RestTemplate restTemplate;
    private final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    private final String vaultUrl;
    private final String token;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VaultSyncService(String vaultUrl, String token, RestTemplate restTemplate, String kvDataPath, String configServerName) {
        this.restTemplate = restTemplate;
        this.taskScheduler.initialize();
        this.vaultUrl = vaultUrl;
        this.token = token;
        this.kvDataPath = kvDataPath;
        this.configServerName = configServerName;
        syncDatabaseCredentials();
    }

    private void syncDatabaseCredentials() {
        cleanUpHangingTasks();
        JsonNode kvData = getVaultAppKvData(configServerName, null);
        VaultUpdateCredentialsProperties properties = objectMapper.convertValue(kvData, VaultUpdateCredentialsProperties.class);
        cancelTasksForDeletedConfigurations(properties);
        properties.applications()
                .forEach(application -> syncDatabaseCredentialsForApplication(application, properties));
        taskScheduler.schedule(
                this::syncDatabaseCredentials,
                getScheduledTimeWithDelay(properties.updateConfigsInterval())
        );
        log.info("Scheduled next global database credential sync in {} seconds.", properties.updateConfigsInterval());
    }

    private void cancelTasksForDeletedConfigurations(VaultUpdateCredentialsProperties properties) {
        scheduledTasks.entrySet().stream()
                .map(entry -> removeTaskIfAppEnvDeleted(entry.getKey(), entry.getValue(), properties))
                .reduce(Integer::sum)
                .ifPresent(numberOfCanceledTasks -> log.info("Canceled {} tasks.", numberOfCanceledTasks));
    }

    private void cleanUpHangingTasks() {
        scheduledTasks.entrySet().stream()
                .map(this::removeTaskIfDoneOrCancelled)
                .reduce(Integer::sum)
                .ifPresent(numberOfCleanedUpTasks -> log.info("Cleaned up {} tasks.", numberOfCleanedUpTasks));
    }

    private Instant getScheduledTimeWithDelay(long delayInSeconds) {
        return Instant.ofEpochMilli(System.currentTimeMillis() + delayInSeconds * 1000L);
    }

    private int removeTaskIfDoneOrCancelled(Map.Entry<String, ScheduledFuture<?>> entry) {
        if (entry.getValue().isDone() || entry.getValue().isCancelled()) {
            scheduledTasks.remove(entry.getKey());
            return 1;
        } else {
            return 0;
        }
    }

    private int removeTaskIfAppEnvDeleted(String name, ScheduledFuture<?> task, VaultUpdateCredentialsProperties properties) {
        if (isApplicationEnvironmentNotInProperties(name, properties)) {
            boolean successfullyCanceled = task.cancel(false);
            if (successfullyCanceled) {
                scheduledTasks.remove(name);
                return 1;
            } else {
                log.warn("Failed to cancel task for application-environment: {}", name);
                return 0;
            }
        } else {
            return 0;
        }
    }

    private boolean isApplicationEnvironmentNotInProperties(String name, VaultUpdateCredentialsProperties properties) {
        return properties.applications().stream()
                .noneMatch(application -> hasApplicationEnvironment(name, application));
    }

    private boolean hasApplicationEnvironment(String name, ApplicationVaultProperties application) {
        return application.environments().stream()
                .anyMatch(environment -> getApplicationEnvironmentName(application.name(), environment.name()).equals(name));
    }

    private void syncDatabaseCredentialsForApplication(ApplicationVaultProperties application, VaultUpdateCredentialsProperties properties) {
        application.environments().stream()
                .filter(environment -> isEnvironmentSelected(environment, properties))
                .filter(environment -> isSyncNotScheduledForEnvironment(application, environment))
                .forEach(environment -> syncDbCredentialsForAppEnvWithRetry(application.name(), environment.name(), environment.role()));
    }

    private boolean isSyncNotScheduledForEnvironment(ApplicationVaultProperties application, ApplicationEnvironmentProperties environment) {
        return !scheduledTasks.containsKey(getApplicationEnvironmentName(application.name(), environment.name()));
    }

    private String getApplicationEnvironmentName(String application, String environment) {
        return String.format(STR_SLASH_STR_FORMAT, application, environment);
    }

    private boolean isEnvironmentSelected(ApplicationEnvironmentProperties environment, VaultUpdateCredentialsProperties properties) {
        return properties.syncAllEnvironments() || properties.environmentsToSync().contains(environment.name());
    }

    private void syncDbCredentialsForAppEnvWithRetry(String appName, String environment, String dbRole) {
        try {
            syncDbCredentialsForAppEnv(appName, environment, dbRole);
        } catch (Exception e) {
            log.error("[{}/{}] Failed to sync database credentials to KV. Retrying in 1 minute.", appName, environment, e);
            rescheduleApplicationTask(60, appName, environment, dbRole);
        }
    }

    private void syncDbCredentialsForAppEnv(String appName, String environment, String dbRole) throws JsonPatchException, JsonProcessingException {
        var response = makeVaultGetRequest("database/creds/" + dbRole);
        var body = objectMapper.valueToTree(Objects.requireNonNull(response.getBody()));
        JsonNode newDataSourceCredentials = readNewCredentialsFromResponse(body);
        int leaseDuration = Objects.requireNonNull(body.get("lease_duration")).asInt();

        // Store credentials in KV store secret for the specified application-environment, after merging with existing data
        JsonNode kvData = getVaultAppKvData(appName, environment);
        JsonNode mergedKvData = JsonMergePatch.fromJson(newDataSourceCredentials).apply(kvData);
        updateVaultAppConfiguration(appName, environment, mergedKvData);
        log.info("[{}/{}] Updated Vault KV with new database credentials.", appName, environment);

        // Dynamically schedule the next update before lease expiry (90% of lease duration)
        long refreshTime = (long) (leaseDuration * 0.9);
        log.info("[{}/{}] Next credential refresh scheduled in {} seconds.", appName, environment, refreshTime);
        rescheduleApplicationTask(refreshTime, appName, environment, dbRole);
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
        return restTemplate.exchange(buildVaultUrlPath(path), HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });
    }

    private String buildVaultUrlPath(String path) {
        return String.format(STR_SLASH_STR_FORMAT, vaultUrl, path);
    }

    private void updateVaultAppConfiguration(String appName, String environment, JsonNode data) throws JsonProcessingException {
        HttpHeaders headers = setupVaultHttpHeaders(true);
        var requestBody = Map.of("data", objectMapper.treeToValue(data, Map.class));
        var entity = new HttpEntity<>(requestBody, headers);
        restTemplate.exchange(buildVaultUrlPath(buildAppConfigPath(appName, environment)), HttpMethod.POST, entity, Map.class);
    }

    private String buildAppConfigPath(String appName, @Nullable String environment) {
        return Optional.ofNullable(environment)
                .map(env -> String.format("%s/%s/%s", kvDataPath, appName, env))
                .orElse(String.format(STR_SLASH_STR_FORMAT, kvDataPath, appName));
    }

    private HttpHeaders setupVaultHttpHeaders(boolean modifying) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_VAULT_TOKEN_HEADER, token);
        if (modifying) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        return headers;
    }

    private void rescheduleApplicationTask(long delayInSeconds, String appName, String environment, String dbRole) {
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
                () -> syncDbCredentialsForAppEnvWithRetry(appName, environment, dbRole),
                getScheduledTimeWithDelay(delayInSeconds)
        );
        scheduledTasks.put(getApplicationEnvironmentName(appName, environment), scheduledTask);
    }
}

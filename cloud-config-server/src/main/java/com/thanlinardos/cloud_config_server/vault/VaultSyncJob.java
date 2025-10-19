package com.thanlinardos.cloud_config_server.vault;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.thanlinardos.cloud_config_server.batch.BatchJobProcessor;
import com.thanlinardos.cloud_config_server.batch.BatchJobRunScheduler;
import com.thanlinardos.cloud_config_server.batch.Task;
import com.thanlinardos.cloud_config_server.vault.properties.batch.VaultSyncJobConfig;
import com.thanlinardos.cloud_config_server.vault.properties.update_credentials.ApplicationEnvironmentProperties;
import com.thanlinardos.cloud_config_server.vault.properties.update_credentials.ApplicationVaultProperties;
import com.thanlinardos.cloud_config_server.vault.properties.update_credentials.VaultUpdateCredentialsProperties;
import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;

@Slf4j
public class VaultSyncJob extends BatchJobProcessor<VaultSyncJobConfig> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public VaultSyncJob(ThreadPoolTaskScheduler taskScheduler, VaultSyncJobConfig config) {
        super(taskScheduler, config);
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    protected String getTaskName(String... args) {
        return VaultIntegrationHelper.getAppEnvName(args[0], args[1]);
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
    protected Instant execute() {
        JsonNode kvData = getVaultAppKvData(getConfig().getConfigServerName(), null);
        VaultUpdateCredentialsProperties properties = objectMapper.convertValue(kvData, VaultUpdateCredentialsProperties.class);
        markTasksForRemoval(properties);
        cancelMarkedTasks();

        properties.applications()
                .forEach(application -> syncDatabaseCredentialsForApplication(application, properties));
        return getNextRunTime(properties);
    }

    private Instant getNextRunTime(VaultUpdateCredentialsProperties properties) {
        Instant nextUpdateConfigsInstant = properties.getNextUpdateConfigsInstant(TimeFactory.getInstant());
        Instant minTaskRunTime = getScheduledTasks().values().stream()
                .filter(Predicate.not(Task::isScheduled))
                .map(Task::getRunTime)
                .filter(Objects::nonNull)
                .min(Instant::compareTo)
                .orElse(nextUpdateConfigsInstant);
        return nextUpdateConfigsInstant.isBefore(minTaskRunTime) ? nextUpdateConfigsInstant : minTaskRunTime;
    }

    private void markTasksForRemoval(VaultUpdateCredentialsProperties properties) {
        getScheduledTasks().values().stream()
                .filter(task -> isApplicationEnvironmentNotInProperties(task.getName(), properties))
                .forEach(task -> task.setMarkedForCancellation(true));
    }

    private JsonNode getVaultAppKvData(String appName, @Nullable String environment) {
        var kvDataResponse = makeVaultGetRequest(VaultIntegrationHelper.buildAppConfigPath(appName, environment));
        return getKvDataJsonFromResponseBody(Objects.requireNonNull(kvDataResponse.getBody()));
    }

    @Nonnull
    private JsonNode getKvDataJsonFromResponseBody(Map<String, Object> body) {
        return objectMapper.valueToTree(body.get("data")).get("data");
    }

    private JsonNode readNewCredentialsFromResponse(JsonNode body) {
        JsonNode credentials = body.get("data");
        String username = Objects.requireNonNull(credentials.get("username")).asText();
        String password = Objects.requireNonNull(credentials.get("password")).asText();
        Map<String, Map<String, String>> datasourceMap = Map.of("datasource", Map.of("username", username, "password", password));
        return objectMapper.valueToTree(datasourceMap);
    }

    private RestTemplate getRestTemplate() {
        return getConfig().getRestTemplate();
    }

    private String getVaultUrl() {
        return getConfig().getVaultUrl();
    }

    private String getToken() {
        return getConfig().getToken();
    }

    private ResponseEntity<Map<String, Object>> makeVaultGetRequest(String path) {
        var entity = VaultIntegrationHelper.setupVaultGetHttpEntity(getToken());
        String url = VaultIntegrationHelper.buildVaultUrlPath(path, getVaultUrl());
        return getRestTemplate().exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });
    }

    private void updateVaultAppConfiguration(String appName, String environment, JsonNode data) throws JsonProcessingException {
        var entity = setupHttpEntityWithData(data, getToken());
        String url = VaultIntegrationHelper.buildVaultUrlPath(appName, environment, getVaultUrl());
        getRestTemplate().exchange(url, HttpMethod.POST, entity, Map.class);
    }

    private HttpEntity<Map<String, Map<?, ?>>> setupHttpEntityWithData(JsonNode data, String token) throws JsonProcessingException {
        HttpHeaders headers = VaultIntegrationHelper.setupVaultHttpHeaders(true, token);
        var body = Map.<String, Map<?, ?>>of("data", objectMapper.treeToValue(data, Map.class));
        return new HttpEntity<>(body, headers);
    }

    private boolean isApplicationEnvironmentNotInProperties(String name, VaultUpdateCredentialsProperties properties) {
        return properties.applications().stream()
                .noneMatch(application -> hasApplicationEnvironment(name, application));
    }

    private boolean hasApplicationEnvironment(String name, ApplicationVaultProperties application) {
        return application.environments().stream()
                .anyMatch(environment -> getTaskName(application.name(), environment.name()).equals(name));
    }

    private void syncDatabaseCredentialsForApplication(ApplicationVaultProperties application, VaultUpdateCredentialsProperties properties) {
        application.environments().stream()
                .filter(properties::isEnvironmentToSync)
                .filter(environment -> isSyncNotScheduledForEnvironment(application, environment))
                .forEach(environment -> syncDbCredentialsForAppEnvWithRetry(application.name(), environment, false));
    }

    private boolean isSyncNotScheduledForEnvironment(ApplicationVaultProperties application, ApplicationEnvironmentProperties environment) {
        String taskName = getTaskName(application.name(), environment.name());
        return isTaskNotScheduled(taskName);
    }

    private void syncDbCredentialsForAppEnvWithRetry(String appName, ApplicationEnvironmentProperties environment, boolean isRetry) {
        try {
            syncDbCredentialsForAppEnv(appName, environment, isRetry);
        } catch (Exception e) {
            retryFailedTask(e, getTaskName(appName, environment.name()), () -> syncDbCredentialsForAppEnvWithRetry(appName, environment, true));
        }
    }

    private void syncDbCredentialsForAppEnv(String appName, ApplicationEnvironmentProperties environment, boolean isRetry) throws JsonPatchException, JsonProcessingException {
        var response = makeVaultGetRequest("database/creds/" + environment.role());
        var body = objectMapper.valueToTree(Objects.requireNonNull(response.getBody()));
        JsonNode newDataSourceCredentials = readNewCredentialsFromResponse(body);
        int leaseDuration = Objects.requireNonNull(body.get("lease_duration")).asInt();

        // Store credentials in KV store secret for the specified application-environment, after merging with existing data
        JsonNode kvData = getVaultAppKvData(appName, environment.name());
        JsonNode mergedKvData = JsonMergePatch.fromJson(newDataSourceCredentials).apply(kvData);
        String taskName = getTaskName(appName, environment.name());
        boolean isKvDataChanged = !mergedKvData.equals(kvData);
        if (isKvDataChanged && isRetryOrNotScheduledForRetry(taskName, isRetry)) {
            updateVaultAppConfiguration(appName, environment.name(), mergedKvData);
            logTaskInfo(appName, environment.name(), "Updated Vault KV with new database credentials.");
            scheduleNextCredentialsUpdate(appName, environment, leaseDuration, taskName);
        } else if (!isKvDataChanged) {
            logTaskInfo(appName, environment.name(), "Skipping task as there are no changes in database credentials.");
        } else {
            logTaskInfo(appName, environment.name(), "Skipping task as it is already scheduled for retry.");
        }
    }

    private void logTaskInfo(String appName, String envName, String message) {
        logTaskInfo(getTaskName(appName, envName), message);
    }

    private void scheduleNextCredentialsUpdate(String appName, ApplicationEnvironmentProperties environment, int leaseDuration, String taskName) {
        long refreshTime = calculateRefreshTime(leaseDuration);
        Instant startTime = TimeFactory.getInstant().plusSeconds(refreshTime);
        if (isOutsideSchedulingWindow(refreshTime)) {
            getScheduledTasks().put(taskName, Task.forRegister(taskName, startTime));
        } else {
            rescheduleTask(() -> syncDbCredentialsForAppEnvWithRetry(appName, environment, false), taskName, startTime);
        }
        logTaskInfo(taskName, "Next credential refresh scheduled in {} seconds.", refreshTime);
    }

    private long calculateRefreshTime(int leaseDuration) {
        return (long) (leaseDuration * (getConfig().getMaxLeaseExpiryPercent() / 100.0));
    }

    private boolean isOutsideSchedulingWindow(long refreshTime) {
        return refreshTime > BatchJobRunScheduler.SCHEDULING_WINDOW_SECONDS;
    }

    private boolean isRetryOrNotScheduledForRetry(String taskName, boolean isRetry) {
        return isRetry || isTaskNotScheduledForRetry(taskName);
    }

    private boolean isTaskNotScheduledForRetry(String taskName) {
        return Optional.ofNullable(getScheduledTasks().get(taskName))
                .filter(Task::isScheduledRetry)
                .isEmpty();
    }
}

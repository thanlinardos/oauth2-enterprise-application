package com.thanlinardos.cloud_config_server.vault;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thanlinardos.cloud_config_server.batch.properties.SchedulerExecutionProperties;
import com.thanlinardos.cloud_config_server.batch.properties.TaskExecutionProperties;
import com.thanlinardos.cloud_config_server.vault.properties.batch.VaultConnectionProperties;
import com.thanlinardos.cloud_config_server.vault.properties.batch.VaultSyncJobConfig;
import com.thanlinardos.spring_enterprise_library.https.SecureHttpRequestFactory;
import com.thanlinardos.spring_enterprise_library.https.properties.KeyAndTrustStoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import static com.thanlinardos.cloud_config_server.vault.VaultIntegrationConstants.VAULT_URL_FORMAT;

@Configuration
@Slf4j
public class VaultConfiguration {

    @Value("${spring.cloud.config.server.vault.token}")
    private String token;
    @Value("${spring.cloud.config.server.vault.host}")
    private String host;
    @Value("${spring.cloud.config.server.vault.port}")
    private String port;
    @Value("${spring.cloud.config.server.vault.scheme}")
    private String scheme;
    @Value("${spring.cloud.config.server.vault.ssl.key-store}")
    private Resource keyStore;
    @Value("${spring.cloud.config.server.vault.ssl.key-store-password}")
    private String keyStorePassword;
    @Value("${spring.cloud.config.server.vault.ssl.trust-store}")
    private Resource trustStore;
    @Value("${spring.cloud.config.server.vault.ssl.trust-store-password}")
    private String trustStorePassword;
    @Value("${spring.application.name}")
    private String configServerName;
    @Value("${batch.vault-sync.backoff-step-size}")
    private int backOffStepSize;
    @Value("${batch.vault-sync.max-delay}")
    private int maxDelay;
    @Value("${batch.vault-sync.max-task-retries}")
    private int maxTaskRetries;
    @Value("${batch.vault-sync.max-execution-attempts}")
    private int maxExecutionAttempts;
    @Value("${batch.vault-sync.max-lease-expiry-percent}")
    private int maxLeaseExpiryPercent;
    @Value("${batch.vault-sync.run-on-startup}")
    private boolean runOnStartup;
    @Value("${batch.run-timer.frequency-ms}")
    private long timerFrequencyMs;
    @Value("${batch.run-timer.schedule-window-seconds}")
    private long timerScheduleWindowSeconds;

    @Bean
    @RefreshScope
    public VaultSyncJob vaultSyncJob(ClientHttpRequestFactory vaultClientRequestFactory, ThreadPoolTaskScheduler taskScheduler, ObjectMapper objectMapper) {
        VaultSyncJobConfig config = getVaultSyncJobConfig(vaultClientRequestFactory);
        return new VaultSyncJob(taskScheduler, config, objectMapper);
    }

    private VaultSyncJobConfig getVaultSyncJobConfig(ClientHttpRequestFactory vaultClientRequestFactory) {
        String vaultUrl = String.format(VAULT_URL_FORMAT, scheme, host, port);
        RestTemplate vaultClientRestTemplate = new RestTemplate(vaultClientRequestFactory);
        SchedulerExecutionProperties schedulerExecutionProperties = new SchedulerExecutionProperties(maxExecutionAttempts, runOnStartup, timerFrequencyMs, timerScheduleWindowSeconds);
        TaskExecutionProperties taskExecutionProperties = new TaskExecutionProperties(backOffStepSize, maxDelay, maxTaskRetries);
        VaultConnectionProperties connectionProperties = new VaultConnectionProperties(configServerName, vaultUrl, token, maxLeaseExpiryPercent);

        return new VaultSyncJobConfig(schedulerExecutionProperties, taskExecutionProperties, connectionProperties, vaultClientRestTemplate);
    }

    @Bean
    @RefreshScope
    public ClientHttpRequestFactory vaultClientRequestFactory() {
        log.info("Creating vault client HTTP request factory with keystore {} and truststore {}", keyStore.getFilename(), trustStore.getFilename());
        KeyAndTrustStoreProperties keystore = new KeyAndTrustStoreProperties(keyStore, keyStorePassword);
        KeyAndTrustStoreProperties truststore = new KeyAndTrustStoreProperties(trustStore, trustStorePassword);
        return new SecureHttpRequestFactory(keystore, truststore);
    }
}
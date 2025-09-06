package com.thanlinardos.cloud_config_server.vault;

import com.thanlinardos.cloud_config_server.batch.properties.TaskExecutionProperties;
import com.thanlinardos.cloud_config_server.vault.properties.batch.VaultConnectionProperties;
import com.thanlinardos.cloud_config_server.vault.properties.batch.VaultSyncJobConfig;
import com.thanlinardos.spring_enterprise_library.https.SecureHttpRequestFactory;
import com.thanlinardos.spring_enterprise_library.https.SslContextUtil;
import com.thanlinardos.spring_enterprise_library.model.properties.KeyAndTrustStoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

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
    @Value("${kv-data-path}")
    private String kvDataPath;
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

    @Bean
    public VaultSyncJob vaultSyncJob(ClientHttpRequestFactory vaultClientRequestFactory, ThreadPoolTaskScheduler taskScheduler) {
        VaultSyncJobConfig config = getVaultSyncJobConfig(vaultClientRequestFactory);
        VaultSyncJob vaultSyncJob = new VaultSyncJob(taskScheduler, config);
        vaultSyncJob.start();
        return vaultSyncJob;
    }

    private VaultSyncJobConfig getVaultSyncJobConfig(ClientHttpRequestFactory vaultClientRequestFactory) {
        String vaultUrl = String.format(VAULT_URL_FORMAT, scheme, host, port);
        RestTemplate vaultClientRestTemplate = new RestTemplate(vaultClientRequestFactory);
        TaskExecutionProperties taskExecutionProperties = new TaskExecutionProperties(backOffStepSize, maxDelay, maxTaskRetries);
        VaultConnectionProperties connectionProperties = new VaultConnectionProperties(configServerName, kvDataPath, vaultUrl, token, maxLeaseExpiryPercent);

        return new VaultSyncJobConfig(maxExecutionAttempts, taskExecutionProperties, connectionProperties, vaultClientRestTemplate);
    }

    @Bean
    public ClientHttpRequestFactory vaultClientRequestFactory() throws UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        KeyAndTrustStoreProperties keystore = new KeyAndTrustStoreProperties(keyStore, keyStorePassword);
        KeyAndTrustStoreProperties truststore = new KeyAndTrustStoreProperties(trustStore, trustStorePassword);
        return new SecureHttpRequestFactory(SslContextUtil.buildSSLContext(keystore, truststore));
    }
}

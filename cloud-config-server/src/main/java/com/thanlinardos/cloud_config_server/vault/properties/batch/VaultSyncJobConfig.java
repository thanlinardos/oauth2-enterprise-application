package com.thanlinardos.cloud_config_server.vault.properties.batch;

import com.thanlinardos.cloud_config_server.batch.properties.BatchJobConfig;
import com.thanlinardos.cloud_config_server.batch.properties.TaskExecutionProperties;
import lombok.Getter;
import org.springframework.web.client.RestTemplate;

@Getter
public class VaultSyncJobConfig extends BatchJobConfig {

    private static final String VAULT_SYNC_JOB = "VAULT_SYNC_JOB";

    private final RestTemplate restTemplate;
    private final VaultConnectionProperties connectionProperties;

    public VaultSyncJobConfig(int maxExecutionAttempts, TaskExecutionProperties taskExecutionProperties, VaultConnectionProperties connectionProperties, RestTemplate restTemplate) {
        super(maxExecutionAttempts, taskExecutionProperties, VAULT_SYNC_JOB);
        this.restTemplate = restTemplate;
        this.connectionProperties = connectionProperties;
    }

    /**
     * The name of the config server as registered in Vault.
     *
     * @return the config server name.
     */
    public String getConfigServerName() {
        return connectionProperties.configServerName();
    }

    /**
     * The path in Vault where the KV secrets engine is mounted.
     *
     * @return the KV data path.
     */
    public String getKvDataPath() {
        return connectionProperties.kvDataPath();
    }

    /**
     * The base URL of the Vault server, e.g. <a href="https://vault.example.com:8200/v1/">https://vault.example.com:8200/v1/</a>
     *
     * @return the Vault URL.
     */
    public String getVaultUrl() {
        return connectionProperties.vaultUrl();
    }

    /**
     * The token used to authenticate with Vault.
     *
     * @return the Vault token.
     */
    public String getToken() {
        return connectionProperties.token();
    }

    /**
     * The maximum percentage of the lease duration after which a secret should be renewed.
     * For example, if a secret has a lease duration of 60 minutes and the max lease expiry percent is set to 80%,
     * the secret will be renewed after 48 minutes (=0.8 * 60).
     *
     * @return the maximum lease expiry percent.
     */
    public int getMaxLeaseExpiryPercent() {
        return connectionProperties.maxLeaseExpiryPercent();
    }
}

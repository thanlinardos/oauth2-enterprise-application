package com.thanlinardos.cloud_config_server.vault;

public class VaultIntegrationConstants {

    private VaultIntegrationConstants() {
    }

    public static final String STR_SLASH_STR_FORMAT = "%s/%s";
    public static final String TRIPLE_STR_SLASH_FORMAT = "%s/%s/%s";
    public static final String X_VAULT_TOKEN_HEADER = "X-Vault-Token";
    public static final String VAULT_URL_FORMAT = "%s://%s:%s/v1/";
    public static final String KV_DATA_PATH = "kv/data";
}

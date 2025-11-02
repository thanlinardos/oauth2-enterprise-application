package com.thanlinardos.cloud_config_server.vault;

import jakarta.annotation.Nullable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Optional;

import static com.thanlinardos.cloud_config_server.vault.VaultIntegrationConstants.KV_DATA_PATH;
import static com.thanlinardos.cloud_config_server.vault.VaultIntegrationConstants.STR_SLASH_STR_FORMAT;
import static com.thanlinardos.cloud_config_server.vault.VaultIntegrationConstants.TRIPLE_STR_SLASH_FORMAT;
import static com.thanlinardos.cloud_config_server.vault.VaultIntegrationConstants.X_VAULT_TOKEN_HEADER;

public class VaultIntegrationHelper {

    private VaultIntegrationHelper() {
    }

    public static String getAppEnvName(String application, String environment) {
        return String.format(STR_SLASH_STR_FORMAT, application, environment);
    }

    public static String buildVaultUrlPath(String appConfigPath, String vaultBaseUrl) {
        return String.format(STR_SLASH_STR_FORMAT, vaultBaseUrl, appConfigPath);
    }

    public static String buildVaultUrlPath(String appName, String environment, String vaultBaseUrl) {
        String appConfigPath = buildAppConfigPath(appName, environment);
        return buildVaultUrlPath(appConfigPath, vaultBaseUrl);
    }

    public static String buildAppConfigPath(String appName, @Nullable String environment) {
        return Optional.ofNullable(environment)
                .map(env -> String.format(TRIPLE_STR_SLASH_FORMAT, KV_DATA_PATH, appName, env))
                .orElse(String.format(STR_SLASH_STR_FORMAT, KV_DATA_PATH, appName));
    }

    public static HttpHeaders setupVaultHttpHeaders(boolean modifying, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_VAULT_TOKEN_HEADER, token);
        if (modifying) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        return headers;
    }

    public static HttpEntity<Object> setupVaultGetHttpEntity(String token) {
        HttpHeaders headers = VaultIntegrationHelper.setupVaultHttpHeaders(false, token);
        return new HttpEntity<>(headers);
    }
}

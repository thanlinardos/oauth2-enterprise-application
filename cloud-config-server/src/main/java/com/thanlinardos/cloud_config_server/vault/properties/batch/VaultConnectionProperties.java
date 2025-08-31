package com.thanlinardos.cloud_config_server.vault.properties.batch;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record VaultConnectionProperties(String configServerName, String kvDataPath, String vaultUrl, String token, @Min(0) @Max(100) int maxLeaseExpiryPercent) {
}

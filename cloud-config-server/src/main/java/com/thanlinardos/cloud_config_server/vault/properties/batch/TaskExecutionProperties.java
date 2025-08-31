package com.thanlinardos.cloud_config_server.vault.properties.batch;

public record TaskExecutionProperties(int backOffStepSize, int maxDelay, int maxTaskRetries) {
}

package com.thanlinardos.cloud_config_server.batch.properties;

public record TaskExecutionProperties(int backOffStepSize, int maxDelay, int maxTaskRetries) {
}

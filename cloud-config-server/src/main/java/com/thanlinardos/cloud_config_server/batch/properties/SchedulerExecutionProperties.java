package com.thanlinardos.cloud_config_server.batch.properties;

public record SchedulerExecutionProperties(int maxExecutionAttempts, boolean runOnStartUp, long timerFrequencyMs, long timerScheduleWindowSeconds) {
}

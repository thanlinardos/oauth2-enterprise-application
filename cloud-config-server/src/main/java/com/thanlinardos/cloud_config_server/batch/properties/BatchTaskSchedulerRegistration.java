package com.thanlinardos.cloud_config_server.batch.properties;

public record BatchTaskSchedulerRegistration<C extends BatchSchedulerConfig>(C config, Runnable runnable) {

    public boolean isRunOnStartUp() {
        return config.isRunOnStartUp();
    }
}

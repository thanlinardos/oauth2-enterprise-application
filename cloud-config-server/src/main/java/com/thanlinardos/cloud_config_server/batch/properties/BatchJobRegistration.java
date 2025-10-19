package com.thanlinardos.cloud_config_server.batch.properties;

public record BatchJobRegistration<C extends BatchJobConfig>(C config, Runnable runnable) {

    public boolean isRunOnStartUp() {
        return config.isRunOnStartUp();
    }
}

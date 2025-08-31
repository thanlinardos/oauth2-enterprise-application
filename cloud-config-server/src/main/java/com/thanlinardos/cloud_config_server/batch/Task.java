package com.thanlinardos.cloud_config_server.batch;

import lombok.*;

import java.util.concurrent.ScheduledFuture;

@Getter
public class Task {

    private final String name;
    private final ScheduledFuture<?> scheduledFuture;
    @Setter
    private int retryCount = 0;
    @Setter
    private boolean isMarkedForCancellation = false;

    public Task(String name, ScheduledFuture<?> scheduledFuture) {
        this.name = name;
        this.scheduledFuture = scheduledFuture;
    }
}

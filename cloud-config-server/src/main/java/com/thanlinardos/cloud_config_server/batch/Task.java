package com.thanlinardos.cloud_config_server.batch;

import lombok.*;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

    /**
     * Check if the task is done.
     *
     * @return true if the task is done, false otherwise.
     */
    public boolean isDone() {
        return scheduledFuture.isDone();
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", retryCount=" + retryCount +
                ", isMarkedForCancellation=" + isMarkedForCancellation +
                ", isDone=" + scheduledFuture.isDone() +
                ", isCanceled=" + scheduledFuture.isCancelled() +
                ", executionTime=" + Instant.now().plusSeconds(scheduledFuture.getDelay(TimeUnit.SECONDS)) +
                '}';
    }
}

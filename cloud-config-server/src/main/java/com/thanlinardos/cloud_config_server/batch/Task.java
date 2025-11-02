package com.thanlinardos.cloud_config_server.batch;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Getter
public class Task {

    private final Instant runTime;
    private final String name;
    @Nullable
    private final ScheduledFuture<?> scheduledFuture;
    private final int retryCount;
    @Setter
    private boolean isMarkedForCancellation = false;

    public Task(String name, @Nullable ScheduledFuture<?> scheduledFuture, int retryCount, Instant runTime) {
        this.name = name;
        this.scheduledFuture = scheduledFuture;
        this.retryCount = retryCount;
        this.runTime = runTime;
    }

    public Task(String name, int retryCount, Instant runTime) {
        this(name, null, retryCount, runTime);
    }

    public static Task forRegister(String name, Instant runTime) {
        return new Task(name, 0, runTime);
    }

    public static Task forReschedule(Task task, ScheduledFuture<?> scheduledFuture) {
        return new Task(task.getName(), scheduledFuture, task.getRetryCount(), task.getRunTime());
    }

    private boolean isDone() {
        return Objects.requireNonNull(scheduledFuture).isDone();
    }

    public boolean isNotScheduledOrIsDone() {
        return scheduledFuture == null || isDone();
    }

    /**
     * Check if the job run has a scheduled future created.
     * This means that the job run has been scheduled to run with the {@link ScheduledExecutorService}.
     *
     * @return true if the job run has a scheduled future, false otherwise.
     */
    public boolean isScheduled() {
        return scheduledFuture != null;
    }

    /**
     * Check if the task is scheduled for a retry.
     * A task is scheduled for a retry if it has been scheduled, and is not done.
     *
     * @return true if the task is scheduled for a retry, false otherwise.
     */
    public boolean isScheduledRetry() {
        return isScheduled() && !isDone() && retryCount > 0;
    }

    /**
     * Check if the scheduled task is done.
     * A task is done if it has been scheduled & it's {@link ScheduledFuture} is either completed or canceled.
     *
     * @return true if the task is done, false otherwise.
     */
    public boolean isScheduledDone() {
        return isScheduled() && isDone();
    }

    /**
     * Check if the scheduled task is canceled.
     * A task is canceled if it has been scheduled & it's {@link ScheduledFuture} is canceled.
     *
     * @return true if the task is canceled, false otherwise.
     */
    public boolean isScheduledCanceled() {
        return isScheduled() && Objects.requireNonNull(scheduledFuture).isCancelled();
    }

    @Override
    public String toString() {
        return "Task{"
                + "name='" + name + '\''
                + ", retryCount=" + retryCount
                + ", isMarkedForCancellation=" + isMarkedForCancellation
                + ", isScheduled=" + isScheduled()
                + ", isDone=" + isScheduledDone()
                + ", isCanceled=" + isScheduledCanceled()
                + ", runTime=" + runTime
                + '}';
    }
}

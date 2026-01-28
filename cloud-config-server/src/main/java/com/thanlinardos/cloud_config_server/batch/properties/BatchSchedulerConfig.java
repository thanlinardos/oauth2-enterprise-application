package com.thanlinardos.cloud_config_server.batch.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class BatchSchedulerConfig {

    private final SchedulerExecutionProperties schedulerExecutionProperties;
    private final TaskExecutionProperties taskExecutionProperties;

    /**
     * The name of the batch scheduler.
     *
     * @return the batch scheduler name.
     */
    public abstract String getName();

    /**
     * Step size in seconds to increase the delay between retries
     *
     * @return step size in seconds.
     */
    public int getBackOffStepSize() {
        return taskExecutionProperties.backOffStepSize();
    }

    /**
     * Maximum delay in seconds between retries.
     *
     * @return maximum delay in seconds.
     */
    public int getMaxDelay() {
        return taskExecutionProperties.maxDelay();
    }

    /**
     * Maximum number of retries for a task.
     *
     * @return maximum number of retries.
     */
    public int getMaxTaskRetries() {
        return taskExecutionProperties.maxTaskRetries();
    }

    /**
     * Maximum number of execution attempts for the batch scheduler.
     *
     * @return maximum number of execution attempts.
     */
    public int getMaxExecutionAttempts() {
        return schedulerExecutionProperties.maxExecutionAttempts();
    }

    /**
     * Whether the batch scheduler should run on application startup.
     *
     * @return true if the batch scheduler should run on startup, false otherwise.
     */
    public boolean isRunOnStartUp() {
        return schedulerExecutionProperties.runOnStartUp();
    }

    /**
     * Frequency in milliseconds at which the batch run timer checks for tasks to schedule.
     *
     * @return frequency in milliseconds.
     */
    public long getTimerFrequencyMs() {
        return schedulerExecutionProperties.timerFrequencyMs();
    }

    /**
     * Scheduling window in seconds within which tasks can be scheduled.
     *
     * @return scheduling window in seconds.
     */
    public long getTimerScheduleWindowSeconds() {
        return schedulerExecutionProperties.timerScheduleWindowSeconds();
    }
}

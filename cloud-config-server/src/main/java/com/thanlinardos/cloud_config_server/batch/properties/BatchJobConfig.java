package com.thanlinardos.cloud_config_server.batch.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class BatchJobConfig {

    private final int maxExecutionAttempts;
    private final TaskExecutionProperties taskExecutionProperties;
    private final boolean runOnStartUp;

    /**
     * The name of the batch job.
     *
     * @return the batch job name.
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
}

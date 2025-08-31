package com.thanlinardos.cloud_config_server.batch;

import com.thanlinardos.spring_enterprise_library.utils.BackOffUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Abstract class for processing batch jobs with scheduling, retry, and error handling capabilities.
 *
 * @param <C> the type of {@link BatchJobConfig} used for configuring the batch job processor.
 */
@Slf4j
public abstract class BatchJobProcessor<C extends BatchJobConfig> {

    protected final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    protected final ConcurrentHashMap<String, Task> scheduledTasks = new ConcurrentHashMap<>();
    protected final C config;
    private Task nextExecution;

    protected BatchJobProcessor(C config) {
        this.taskScheduler.initialize();
        this.config = config;
    }

    /**
     * Start the batch job processor with the following steps:
     * 1. Clean up hanging tasks that are done or cancelled.
     * 2. Execute the main logic of the batch job processor.
     * 3. If the execution is successful, schedule the next run based on the returned delay.
     * 4. If the execution fails, log the error, forcefully stop all tasks & schedule a retry attempt with exponential backoff.
     */
    public void start() {
        cleanUpHangingTasks();
        try {
            long nextRunDelay = execute();
            if (nextRunDelay > 0) {
                scheduleNextRun(nextRunDelay);
            }
        } catch (Exception e) {
            log.error("[{}] Failed to execute batch job processor.", config.getName(), e);
            stopAllTasks();
            scheduleRunAttempt();
        }
    }

    /**
     * The main logic of the batch job processor.
     *
     * @return the delay in seconds until the next scheduled execution of the batch job processor.
     */
    protected abstract long execute();

    private void scheduleNextRun(long nextRunDelayInSeconds) {
        scheduleTask(nextRunDelayInSeconds, this::start);
        log.info("[{}] Scheduled next batch job execution in {} seconds.", config.getName(), nextRunDelayInSeconds);
    }

    private void scheduleTask(long nextRunDelayInSeconds, Runnable runnable) {
        scheduleTask(nextRunDelayInSeconds, -1, runnable);
    }

    private void scheduleRunAttempt() {
        int currentAttempt = getCurrentExecutionAttempt();
        if (currentAttempt >= config.getMaxExecutionAttempts()) {
            log.error("[{}] Reached maximum number of retries ({}). Will not attempt to retry the batch job again.", config.getName(), config.getMaxExecutionAttempts());
        } else {
            long backoffDelay = getExponentialBackoffDelay(currentAttempt);
            nextExecution = scheduleTask(backoffDelay, currentAttempt, this::start);
            log.info("[{}] Scheduled retry attempt #{} for batch job execution in {} seconds.", config.getName(), nextExecution.getRetryCount(), backoffDelay);
        }
    }

    private Integer getCurrentExecutionAttempt() {
        return Optional.ofNullable(nextExecution)
                .map(Task::getRetryCount)
                .orElse(0);
    }

    private void stopAllTasks() {
        scheduledTasks.values()
                .forEach(task -> task.getScheduledFuture().cancel(true));
        scheduledTasks.clear();
    }

    private Task scheduleTask(long backoffDelay, int currentAttempt, Runnable runnable) {
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(
                runnable,
                Instant.now().plusSeconds(backoffDelay)
        );
        Task task = new Task(config.getName(), scheduledFuture);
        task.setRetryCount(currentAttempt + 1);
        return task;
    }

    /**
     * Remove tasks that are done or cancelled from the scheduled tasks map.
     * This method is called before scheduling a new task to ensure that the map only contains active tasks.
     */
    private void cleanUpHangingTasks() {
        scheduledTasks.entrySet().stream()
                .map(this::removeTaskIfDoneOrCancelled)
                .reduce(Integer::sum)
                .ifPresent(numberOfCleanedUpTasks -> log.info("Cleaned up {} tasks.", numberOfCleanedUpTasks));
    }

    /**
     * Cancel all tasks that are marked for cancellation.
     * The tasks that are successfully canceled will be removed from the scheduled tasks map.
     */
    protected void cancelMarkedTasks() {
        scheduledTasks.values().stream()
                .map(this::cancelTaskIfMarked)
                .reduce(Integer::sum)
                .ifPresent(numberOfCanceledTasks -> log.info("Canceled {} tasks.", numberOfCanceledTasks));
    }

    /**
     * Check if a task with the given name is not already scheduled.
     *
     * @param taskName the unique name of the task.
     * @return true if the task is not scheduled, false otherwise.
     */
    protected boolean isTaskNotScheduled(String taskName) {
        return !scheduledTasks.containsKey(taskName);
    }

    /**
     * Log the error and reschedule the failed task with exponential backoff delay.
     * If the maximum number of retries is reached, the task will not be retried again.
     *
     * @param e        the exception that caused the task to fail.
     * @param taskName the unique name of the task.
     * @param runnable the {@link Runnable} task to execute.
     */
    protected void logErrorAndRetryFailedTask(Exception e, String taskName, Runnable runnable) {
        int retryCount = scheduledTasks.get(taskName).getRetryCount();
        if (retryCount >= config.getMaxTaskRetries()) {
            log.error("[{}] Failed to execute task with error:", taskName, e);
            log.error("[{}] Reached maximum number of retries ({}). Will not attempt to retry the task again.", taskName, config.getMaxTaskRetries());
            scheduledTasks.remove(taskName);
        } else {
            long delayInSeconds = getExponentialBackoffDelay(retryCount);
            logError(e, retryCount, delayInSeconds, taskName);
            rescheduleTask(retryCount, delayInSeconds, runnable, taskName);
        }
    }

    /**
     * Reschedule a task as the first attempt.
     *
     * @param delayInSeconds delay in seconds before executing the task.
     * @param runnable       the {@link Runnable} task to execute.
     * @param taskName       the unique name of the task.
     */
    protected void rescheduleTask(long delayInSeconds, Runnable runnable, String taskName) {
        rescheduleTask(-1, delayInSeconds, runnable, taskName);
    }

    private int cancelTaskIfMarked(Task task) {
        if (task.isMarkedForCancellation()) {
            boolean successfullyCanceled = task.getScheduledFuture().cancel(false);
            if (successfullyCanceled) {
                scheduledTasks.remove(task.getName());
                return 1;
            } else {
                log.warn("Failed to cancel task with name: {}", task.getName());
                return 0;
            }
        } else {
            return 0;
        }
    }

    private int removeTaskIfDoneOrCancelled(Map.Entry<String, Task> entry) {
        ScheduledFuture<?> scheduledFuture = entry.getValue().getScheduledFuture();
        if (scheduledFuture.isDone() || scheduledFuture.isCancelled()) {
            scheduledTasks.remove(entry.getKey());
            return 1;
        } else {
            return 0;
        }
    }

    private void logError(Exception e, int retryCount, long delayInSeconds, String taskName) {
        String retryMsg = retryCount > 0 ? " after " + retryCount + " retries" : "";
        log.error("[{}] Failed to execute task{}. Retrying in {} seconds.", taskName, retryMsg, delayInSeconds, e);
    }

    private long getExponentialBackoffDelay(int retryCount) {
        return BackOffUtils.getExponentialBackoffDelay(retryCount, config.getBackOffStepSize(), config.getMaxDelay());
    }

    private void rescheduleTask(int retryCount, long delayInSeconds, Runnable runnable, String taskName) {
        Task task = scheduleTask(delayInSeconds, retryCount, runnable);
        scheduledTasks.put(taskName, task);
    }
}


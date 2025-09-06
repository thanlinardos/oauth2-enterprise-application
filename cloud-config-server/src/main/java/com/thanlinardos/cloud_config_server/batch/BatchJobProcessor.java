package com.thanlinardos.cloud_config_server.batch;

import com.thanlinardos.cloud_config_server.batch.properties.BatchJobConfig;
import com.thanlinardos.spring_enterprise_library.utils.BackOffUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

/**
 * Abstract class for processing batch jobs with scheduling, retry, and error handling capabilities.
 *
 * @param <C> the type of {@link BatchJobConfig} used for configuring the batch job processor.
 */
@Slf4j
public abstract class BatchJobProcessor<C extends BatchJobConfig> {

    protected final ThreadPoolTaskScheduler taskScheduler;
    protected final ConcurrentHashMap<String, Task> scheduledTasks = new ConcurrentHashMap<>();
    protected final C config;
    private Task nextExecution;

    protected BatchJobProcessor(ThreadPoolTaskScheduler taskScheduler, C config) {
        this.taskScheduler = taskScheduler;
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
        cleanUpFinishedTasks();
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
        nextExecution = scheduleRun(nextRunDelayInSeconds, this::start);
        log.info("[{}] Scheduled next batch job execution in {} seconds.", config.getName(), nextRunDelayInSeconds);
    }

    private Task scheduleRun(long nextRunDelayInSeconds, Runnable runnable) {
        return scheduleTask(nextRunDelayInSeconds, -1, runnable, config.getName());
    }

    private void scheduleRunAttempt() {
        int currentAttempt = getCurrentExecutionAttempt();
        if (currentAttempt >= config.getMaxExecutionAttempts()) {
            log.error("[{}] Reached maximum number of retries ({}). Will not attempt to retry the batch job again.", config.getName(), config.getMaxExecutionAttempts());
        } else {
            long backoffDelay = getExponentialBackoffDelay(currentAttempt);
            nextExecution = scheduleTask(backoffDelay, currentAttempt, this::start, config.getName());
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

    private Task scheduleTask(long delayInSeconds, int currentAttempt, Runnable runnable, String name) {
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(
                runnable,
                Instant.now().plusSeconds(delayInSeconds)
        );
        Task task = new Task(name, scheduledFuture);
        task.setRetryCount(currentAttempt + 1);
        return task;
    }

    /**
     * Removes tasks that are finished from the scheduled tasks map and logs the number of cleaned up tasks.
     * All completed, canceled & failed tasks are removed.
     * It is called before scheduling a new task to ensure that the map only contains active tasks.
     */
    private void cleanUpFinishedTasks() {
        long numberOfCleanedUpTasks = getFinishedTaskNames().stream()
                .map(scheduledTasks::remove)
                .filter(Objects::nonNull)
                .count();
        log.info("Cleaned up {} tasks.", numberOfCleanedUpTasks);
    }

    private Set<String> getFinishedTaskNames() {
        return scheduledTasks.values().stream()
                .filter(Task::isDone)
                .map(Task::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Cancel all tasks that are marked for cancellation.
     * The tasks that are successfully canceled will be removed from the scheduled tasks map.
     */
    protected void cancelMarkedTasks() {
        log.trace("scheduled tasks: {}", scheduledTasks);
        scheduledTasks.values().stream()
                .map(this::cancelTaskIfMarked)
                .reduce(Integer::sum)
                .ifPresent(numberOfCanceledTasks -> log.info("Canceled {}/{} tasks.", numberOfCanceledTasks, scheduledTasks.size()));
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
    protected void retryFailedTask(Exception e, String taskName, Runnable runnable) {
        Task task = scheduledTasks.get(taskName);
        log.error("[{}] Failed to execute task {} with error:", taskName, getRetryMsg(task), e);
        Optional.ofNullable(task)
                .ifPresentOrElse(t -> retryFoundFailedTask(runnable, t),
                        () -> log.error("Task with name: {} is not found in scheduled tasks. Cannot retry the task.", taskName));
    }

    private String getRetryMsg(Task task) {
        return Optional.ofNullable(task)
                .map(Task::getRetryCount)
                .map(r -> r > 0 ? String.format("after %d retries", r) : "")
                .orElse("");
    }

    private void retryFoundFailedTask(Runnable runnable, Task task) {
        int retryCount = task.getRetryCount();
        if (retryCount >= config.getMaxTaskRetries()) {
            log.error("[{}] Reached maximum number of retries ({}). Will not attempt to retry the task again.", task.getName(), config.getMaxTaskRetries());
            scheduledTasks.remove(task.getName());
        } else {
            long delayInSeconds = getExponentialBackoffDelay(retryCount);
            rescheduleTask(retryCount, delayInSeconds, runnable, task.getName());
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

    private long getExponentialBackoffDelay(int retryCount) {
        return BackOffUtils.getExponentialBackoffDelay(retryCount, config.getBackOffStepSize(), config.getMaxDelay());
    }

    private void rescheduleTask(int retryCount, long delayInSeconds, Runnable runnable, String taskName) {
        Task task = scheduleTask(delayInSeconds, retryCount, runnable, taskName);
        scheduledTasks.put(taskName, task);
    }
}


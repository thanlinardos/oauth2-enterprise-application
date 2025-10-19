package com.thanlinardos.cloud_config_server.batch;

import com.thanlinardos.cloud_config_server.batch.properties.BatchJobConfig;
import com.thanlinardos.spring_enterprise_library.math.utils.BackOffUtils;
import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

/**
 * Abstract class for processing batch jobs with scheduling, retry, and error handling capabilities.
 *
 * @param <C> the type of {@link BatchJobConfig} used for configuring the batch job processor.
 */
@RequiredArgsConstructor
public abstract class BatchJobProcessor<C extends BatchJobConfig> {

    private final ThreadPoolTaskScheduler taskScheduler;
    @Getter
    private final ConcurrentHashMap<String, Task> scheduledTasks = new ConcurrentHashMap<>();
    @Getter
    private final C config;

    protected abstract Logger getLogger();

    /**
     * Start the batch job processor with the following steps:
     * 1. Clean up hanging tasks that are done or cancelled.
     * 2. Execute the main logic of the batch job processor.
     * 3. If the execution is successful, schedule the next run based on the returned delay.
     * 4. If the execution fails, log the error, forcefully stop all tasks & schedule a retry attempt with exponential backoff.
     */
    public void start() {
        long start = System.currentTimeMillis();
        cleanUpFinishedTasks();
        try {
            Instant nextRunTime = execute();
            scheduleNextRun(nextRunTime);
        } catch (Exception e) {
            logJobError("Failed to execute batch job processor.", e);
            stopAllTasks();
            scheduleRunAttempt();
        }
        long executionTIme = System.currentTimeMillis() - start;
        logJobInfo("Batch job processor finished in {} ms.", executionTIme);
    }

    /**
     * The main logic of the batch job processor.
     *
     * @return the delay in seconds until the next scheduled execution of the batch job processor.
     */
    protected abstract Instant execute();

    /**
     * Get the unique name of the batch job.
     *
     * @return the name of the batch job.
     */
    public String getName() {
        return config.getName();
    }

    private void scheduleNextRun(Instant nextRunTime) {
        Instant now = TimeFactory.getInstant();
        Task nextRun = scheduleAndRegisterRun(nextRunTime, -1, now);
        long secondsFromNextRun = nextRunTime.getEpochSecond() - now.getEpochSecond();
        logJobInfo("{} next batch job execution in {} seconds.", getScheduleMsg(nextRun), secondsFromNextRun);
    }

    private String getScheduleMsg(Task nextRun) {
        return nextRun.isScheduled() ? "Scheduled" : "Registered in scheduler";
    }

    private void scheduleRunAttempt() {
        int currentAttempt = getCurrentRunAttempt();
        if (currentAttempt >= config.getMaxExecutionAttempts()) {
            logJobError("Reached maximum number of retries ({}). Will not attempt to retry the batch job again.", config.getMaxExecutionAttempts());
        } else {
            long backoffDelay = getExponentialBackoffDelay(currentAttempt);
            Instant now = TimeFactory.getInstant();
            Instant nextRunTime = now.plusSeconds(backoffDelay);
            Task nextRun = scheduleAndRegisterRun(nextRunTime, currentAttempt, now);
            logJobInfo("{} retry attempt #{} for batch job execution in {} seconds.", getScheduleMsg(nextRun), nextRun.getRetryCount(), backoffDelay);
        }
    }

    private Task scheduleAndRegisterRun(Instant nextRunTime, int currentAttempt, Instant now) {
        Task nextRun = scheduleRun(nextRunTime, this::start, currentAttempt, now);
        registerRun(nextRun);
        return nextRun;
    }

    private void registerRun(Task jobRun) {
        BatchJobRunScheduler.getBatchJobExecutions().put(getName(), jobRun);
    }

    private Task scheduleRun(Instant nextRunTime, Runnable runnable, int currentAttempt, Instant now) {
        if (nextRunTime.isBefore(now.plusMillis(BatchJobRunScheduler.BATCH_JOB_RUN_SCHEDULER_FREQ))) {
            return scheduleTask(currentAttempt, runnable, getName(), nextRunTime);
        } else {
            return new Task(getName(), currentAttempt + 1, nextRunTime);
        }
    }

    private Integer getCurrentRunAttempt() {
        return Optional.ofNullable(getCurrentRun())
                .map(Task::getRetryCount)
                .orElse(0);
    }

    @Nullable
    private Task getCurrentRun() {
        return BatchJobRunScheduler.getBatchJobExecutions().get(getName());
    }

    private void stopAllTasks() {
        scheduledTasks.values().stream()
                .map(Task::getScheduledFuture)
                .filter(Objects::nonNull)
                .forEach(future -> future.cancel(true));
        scheduledTasks.clear();
    }

    private Task scheduleTask(int currentAttempt, Runnable runnable, String name, Instant startTime) {
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(runnable, startTime);
        return new Task(name, scheduledFuture, currentAttempt + 1, startTime);
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
        logJobInfo("Cleaned up {} tasks.", numberOfCleanedUpTasks);
    }

    private Set<String> getFinishedTaskNames() {
        return scheduledTasks.values().stream()
                .filter(Task::isNotScheduledOrIsDone)
                .map(Task::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Cancel all tasks that are marked for cancellation.
     * The tasks that are successfully canceled will be removed from the scheduled tasks map.
     */
    protected void cancelMarkedTasks() {
        logJobTrace("Scheduled tasks: {}", scheduledTasks);
        scheduledTasks.values().stream()
                .filter(Task::isMarkedForCancellation)
                .map(this::cancelMarkedTask)
                .reduce(Integer::sum)
                .ifPresent(numberOfCanceledTasks -> logJobInfo("Canceled {}/{} tasks.", numberOfCanceledTasks, scheduledTasks.size()));
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
        logTaskError(taskName, "Failed to execute task {} with error:", getRetryMsg(task), e);
        Optional.ofNullable(task)
                .ifPresentOrElse(t -> retryFoundFailedTask(runnable, t),
                        () -> logTaskError(taskName, "Task not found in scheduled tasks. Cannot retry the task."));
    }

    private String getRetryMsg(Task task) {
        return Optional.ofNullable(task)
                .map(Task::getRetryCount)
                .filter(r -> r > 0)
                .map(r -> String.format("after %d retries", r))
                .orElse("");
    }

    private void retryFoundFailedTask(Runnable runnable, Task task) {
        int retryCount = task.getRetryCount();
        if (retryCount >= config.getMaxTaskRetries()) {
            logTaskError(task.getName(), "Reached maximum number of retries ({}). Task will not be retried again.", config.getMaxTaskRetries());
            scheduledTasks.remove(task.getName());
        } else {
            long delayInSeconds = getExponentialBackoffDelay(retryCount);
            Instant startTime = TimeFactory.getInstant().plusSeconds(delayInSeconds);
            scheduleAndSaveTask(retryCount, runnable, task.getName(), startTime);
        }
    }

    /**
     * Reschedule a task as the first attempt.
     *
     * @param runnable       the {@link Runnable} task to execute.
     * @param taskName       the unique name of the task.
     * @param startTime      the {@link Instant} to start the task.
     */
    protected void rescheduleTask(Runnable runnable, String taskName, Instant startTime) {
        scheduleAndSaveTask(-1, runnable, taskName, startTime);
    }

    /**
     * Get the unique name of the task based on the provided arguments.
     *
     * @param args the arguments to generate the task name.
     * @return the unique name of the task.
     */
    protected abstract String getTaskName(String... args);

    private int cancelMarkedTask(Task task) {
        boolean successfullyCanceled = Optional.ofNullable(task.getScheduledFuture())
                .map(future -> future.cancel(false))
                .orElse(true);
        if (successfullyCanceled) {
            scheduledTasks.remove(task.getName());
            return 1;
        } else {
            logTaskWarn(task.getName(), "Failed to cancel task.");
            return 0;
        }
    }

    private long getExponentialBackoffDelay(int retryCount) {
        return BackOffUtils.getExponentialBackoffDelay(retryCount, config.getBackOffStepSize(), config.getMaxDelay());
    }

    private void scheduleAndSaveTask(int retryCount, Runnable runnable, String taskName, Instant startTime) {
        Task task = scheduleTask(retryCount, runnable, taskName, startTime);
        scheduledTasks.put(taskName, task);
    }

    protected void logJobInfo(String message, Object... args) {
        logTaskLevel(getName(), message, Level.INFO, args);
    }

    protected void logJobError(String message, Object... args) {
        logTaskLevel(getName(), message, Level.ERROR, args);
    }

    protected void logJobTrace(String message, Object... args) {
        logTaskLevel(getName(), message, Level.TRACE, args);
    }

    protected void logTaskInfo(String taskName, String message, Object... args) {
        logTaskLevel(taskName, message, Level.INFO, args);
    }

    protected void logTaskError(String taskName, String message, Object... args) {
        logTaskLevel(taskName, message, Level.ERROR, args);
    }

    protected void logTaskWarn(String taskName, String message, Object... args) {
        logTaskLevel(taskName, message, Level.WARN, args);
    }

    protected void logTaskLevel(String taskName, String message, Level level, Object... args) {
        getLogger().atLevel(level).log(String.format("[%s] %s", taskName, message), args);
    }
}


package com.thanlinardos.cloud_config_server.batch;

import com.thanlinardos.cloud_config_server.batch.properties.BatchJobRegistration;
import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import com.thanlinardos.spring_enterprise_library.time.model.InstantInterval;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Schedules batch job runs based on their configuration and execution status:
 * <p>
 *  - Maintains a map of currently scheduled and completed job runs.
 *  <p>
 *  - Runs periodically and schedules new job runs within a defined scheduling window.
 */
@Component
@Slf4j
public final class BatchJobRunScheduler {

    public static final int BATCH_JOB_RUN_SCHEDULER_FREQ = 5000;
    public static final int SCHEDULING_WINDOW_SECONDS = 120;
    private static final ConcurrentMap<String, Task> BATCH_JOB_EXECUTIONS = new ConcurrentHashMap<>();

    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<String, BatchJobRegistration<?>> registeredJobs;

    public BatchJobRunScheduler(ThreadPoolTaskScheduler taskScheduler, Map<String, BatchJobRegistration<?>> registeredJobs) {
        this.taskScheduler = taskScheduler;
        this.registeredJobs = registeredJobs;
        initJobRuns();
    }

    private void initJobRuns() {
        this.registeredJobs.values().stream()
                .filter(BatchJobRegistration::isRunOnStartUp)
                .forEach(this::scheduleJobRunOnStartUp);
    }

    private void scheduleJobRunOnStartUp(BatchJobRegistration<?> registration) {
        String jobName = registration.config().getName();
        Instant now = TimeFactory.getInstant();
        ScheduledFuture<?> scheduledFuture = this.taskScheduler.schedule(registration.runnable(), now);
        Task jobRun = new Task(jobName, scheduledFuture, 0, now);
        BATCH_JOB_EXECUTIONS.put(jobName, jobRun);
        log.info("Scheduled job '{}' to run on startup.", jobName);
    }

    public static ConcurrentMap<String, Task> getBatchJobExecutions() {
        return BATCH_JOB_EXECUTIONS;
    }

    @Scheduled(fixedDelay = BATCH_JOB_RUN_SCHEDULER_FREQ, initialDelay = BATCH_JOB_RUN_SCHEDULER_FREQ)
    public void scheduleBatchJobRuns() {
        Map<String, Task> newJobRuns = BATCH_JOB_EXECUTIONS.values().stream()
                .filter(Predicate.not(Task::isScheduled))
                .filter(this::isInsideSchedulingWindow)
                .map(this::scheduleBatchJobRun)
                .collect(Collectors.toMap(Task::getName, Function.identity()));
        BATCH_JOB_EXECUTIONS.putAll(newJobRuns);
    }

    private Task scheduleBatchJobRun(Task run) {
        Runnable runnable = registeredJobs.get(run.getName()).runnable();
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(runnable, run.getRunTime());

        Task newRun = Task.forReschedule(run, scheduledFuture);
        log.info("Scheduled new job run: {}", newRun);
        return newRun;
    }

    private boolean isInsideSchedulingWindow(Task run) {
        Instant now = TimeFactory.getInstant();
        InstantInterval schedulingWindow = new InstantInterval(now, now.plusSeconds(SCHEDULING_WINDOW_SECONDS));
        return schedulingWindow.contains(run.getRunTime());
    }
}

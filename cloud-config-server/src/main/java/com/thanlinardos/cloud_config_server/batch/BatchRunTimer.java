package com.thanlinardos.cloud_config_server.batch;

import com.thanlinardos.cloud_config_server.batch.properties.BatchTaskSchedulerRegistration;
import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import com.thanlinardos.spring_enterprise_library.time.model.InstantInterval;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
 * Schedules batch runs of the {@link BatchTaskScheduler} based on their configuration and execution status:
 * <p>
 * - Maintains a map of currently scheduled and completed runs.
 * <p>
 * - Runs periodically and schedules new runs within a defined scheduling window.
 */
@Component
@Slf4j
@ConditionalOnProperty(value = {"batch.run-timer.enabled"}, havingValue = "true")
public final class BatchRunTimer {

    private static final ConcurrentMap<String, Task> BATCH_RUNS = new ConcurrentHashMap<>();

    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<String, BatchTaskSchedulerRegistration<?>> registeredSchedulers;

    @Value("${batch.run-timer.schedule-window-seconds}")
    private long schedulingWindowSeconds;

    public BatchRunTimer(ThreadPoolTaskScheduler taskScheduler, Map<String, BatchTaskSchedulerRegistration<?>> registeredSchedulers) {
        this.taskScheduler = taskScheduler;
        this.registeredSchedulers = registeredSchedulers;
        initRuns();
    }

    private void initRuns() {
        this.registeredSchedulers.values().stream()
                .filter(BatchTaskSchedulerRegistration::isRunOnStartUp)
                .forEach(this::scheduleRunOnStartUp);
    }

    private void scheduleRunOnStartUp(BatchTaskSchedulerRegistration<?> registration) {
        String schedulerName = registration.config().getName();
        Instant now = TimeFactory.getInstant();
        ScheduledFuture<?> scheduledFuture = this.taskScheduler.schedule(registration.runnable(), now);
        Task run = new Task(schedulerName, scheduledFuture, 0, now);
        BATCH_RUNS.put(schedulerName, run);
        log.info("Scheduled task scheduler '{}' to run on startup.", schedulerName);
    }

    public static ConcurrentMap<String, Task> getBatchRuns() {
        return BATCH_RUNS;
    }

    @Scheduled(fixedDelayString = "${batch.run-timer.frequency-ms}", initialDelayString = "${batch.run-timer.initial-delay-ms}")
    public void scheduleRuns() {
        Map<String, Task> newJobRuns = BATCH_RUNS.values().stream()
                .filter(Predicate.not(Task::isScheduled))
                .filter(this::isInsideSchedulingWindow)
                .map(this::scheduleRun)
                .collect(Collectors.toMap(Task::getName, Function.identity()));
        BATCH_RUNS.putAll(newJobRuns);
    }

    private Task scheduleRun(Task run) {
        Runnable runnable = registeredSchedulers.get(run.getName()).runnable();
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(runnable, run.getRunTime());

        Task newRun = Task.forReschedule(run, scheduledFuture);
        log.info("Scheduled new job run: {}", newRun);
        return newRun;
    }

    private boolean isInsideSchedulingWindow(Task run) {
        Instant now = TimeFactory.getInstant();
        InstantInterval schedulingWindow = new InstantInterval(now, now.plusSeconds(schedulingWindowSeconds));
        return schedulingWindow.contains(run.getRunTime());
    }
}

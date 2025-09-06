package com.thanlinardos.cloud_config_server.batch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class BatchJobConfiguration {

    @Value("#{T(Integer).parseInt('${batch.task-scheduler.pool-size}')}")
    private int poolSize;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(poolSize);
        taskScheduler.setVirtualThreads(true);
        taskScheduler.setThreadNamePrefix("BatchJob-");
        taskScheduler.initialize();
        return taskScheduler;
    }
}

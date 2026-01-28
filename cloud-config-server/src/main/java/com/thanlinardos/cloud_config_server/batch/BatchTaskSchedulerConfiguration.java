package com.thanlinardos.cloud_config_server.batch;

import com.thanlinardos.cloud_config_server.batch.properties.BatchTaskSchedulerRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class BatchTaskSchedulerConfiguration {

    @Bean
    public Map<String, BatchTaskSchedulerRegistration<?>> registeredSchedulers(ApplicationContext context) {
        return context.getBeansOfType(BatchTaskScheduler.class, false, true).values().stream()
                .collect(Collectors.toMap(BatchTaskScheduler::getName, processor -> new BatchTaskSchedulerRegistration<>(processor.getConfig(), processor::start)));
    }
}

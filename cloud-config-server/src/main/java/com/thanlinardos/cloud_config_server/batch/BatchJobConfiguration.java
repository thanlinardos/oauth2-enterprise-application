package com.thanlinardos.cloud_config_server.batch;

import com.thanlinardos.cloud_config_server.batch.properties.BatchJobRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class BatchJobConfiguration {

    @Bean
    public Map<String, BatchJobRegistration<?>> registeredJobs(ApplicationContext context) {
        return context.getBeansOfType(BatchJobProcessor.class).values().stream()
                .collect(Collectors.toMap(BatchJobProcessor::getName, processor -> new BatchJobRegistration<>(processor.getConfig(), processor::start)));
    }
}

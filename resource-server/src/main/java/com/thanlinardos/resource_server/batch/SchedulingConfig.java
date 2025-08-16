package com.thanlinardos.resource_server.batch;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(value = "scheduling.enabled", havingValue = "true")
public class SchedulingConfig {
}

package com.thanlinardos.resource_server.environment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.refresh.ConfigDataContextRefresher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(value = {"scheduling.enabled", "secrets.refresh.enabled"}, havingValue = "true")
public class CloudClientConfigRefreshComponent {

    private final CustomConfigDataContextRefresher contextRefresher;

    CloudClientConfigRefreshComponent(ConfigDataContextRefresher configDataContextRefresher) {
        this.contextRefresher = (CustomConfigDataContextRefresher) configDataContextRefresher;
    }

    @Scheduled(initialDelayString = "${secrets.refresh-interval-ms}", fixedDelayString = "${secrets.refresh-interval-ms}")
    void refresher() {
        Object props = contextRefresher.customRefresh();
        log.info("Refreshed the following props: {}", props);
    }
}

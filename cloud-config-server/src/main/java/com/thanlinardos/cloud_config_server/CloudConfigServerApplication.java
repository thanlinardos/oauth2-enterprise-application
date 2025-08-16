package com.thanlinardos.cloud_config_server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
@EnableConfigurationProperties
@Slf4j
public class CloudConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudConfigServerApplication.class, args);
    }
}

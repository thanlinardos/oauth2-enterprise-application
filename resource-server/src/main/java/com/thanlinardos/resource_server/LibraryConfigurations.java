package com.thanlinardos.resource_server;

import com.thanlinardos.spring_enterprise_library.time.properties.TimeProviderProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.thanlinardos.spring_enterprise_library")
@EnableConfigurationProperties(TimeProviderProperties.class)
public class LibraryConfigurations {
}

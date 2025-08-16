package com.thanlinardos.resource_server.environment;

import com.thanlinardos.resource_server.model.properties.CustomDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.refresh.ConfigDataContextRefresher;
import org.springframework.cloud.util.ConditionalOnBootstrapDisabled;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@Slf4j
@ConditionalOnProperty(value = {"spring.cloud.config.enabled"}, havingValue = "true")
public class CloudConfig {

    @Bean
    @RefreshScope
    public CustomDataSourceProperties customDataSourceProperties(@Value("${datasource.url}") String url,
                                                                 @Value("${datasource.username}") String username,
                                                                 @Value("${datasource.password}") String password,
                                                                 @Value("${datasource.driver-class-name}") String driverClassName) {
        return new CustomDataSourceProperties(url, username, password, driverClassName);
    }

    @Bean
    @Primary
    @RefreshScope
    public DataSource dataSource(CustomDataSourceProperties properties) {
        log.info("Building DataSource for user {} and url {}", properties.getUsername(), properties.getUrl());
        return DataSourceBuilder.create()
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .driverClassName(properties.getDriverClassName())
                .build();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnBootstrapDisabled
    public ConfigDataContextRefresher configDataContextRefresher(ConfigurableApplicationContext context,
                                                                 org.springframework.cloud.context.scope.refresh.RefreshScope scope,
                                                                 RefreshAutoConfiguration.RefreshProperties properties,
                                                                 @Value("${secrets.refresh.displayValues}") boolean displayValues) {
        return new CustomConfigDataContextRefresher(context, scope, properties, displayValues);
    }
}

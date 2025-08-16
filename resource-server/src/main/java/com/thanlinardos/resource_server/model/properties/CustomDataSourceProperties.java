package com.thanlinardos.resource_server.model.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "datasource")
@Getter
@AllArgsConstructor
public class CustomDataSourceProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}

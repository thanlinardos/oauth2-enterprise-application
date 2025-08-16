package com.thanlinardos.resource_server.model.properties.keycloak;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "oauth2.keycloak"
)
@Getter
@AllArgsConstructor
public class KeycloakProperties {

    private String url;
    private String realm;
    private KeycloakClientProperties client;
}

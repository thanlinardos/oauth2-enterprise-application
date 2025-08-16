package com.thanlinardos.resource_server.security.keycloak;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "integration.test.enabled", havingValue = "true", matchIfMissing = true)
public class KeycloakAdminMockConfig {

    @Value("${oauth2.keycloak.url}")
    private String authServerUrl;

    @Value("${oauth2.keycloak.realm}")
    private String realm;

    @Value("${oauth2.keycloak.client.id}")
    private String clientId;

    @Value("${oauth2.keycloak.client.secret}")
    private String clientSecret;

    @Bean
    Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }
}

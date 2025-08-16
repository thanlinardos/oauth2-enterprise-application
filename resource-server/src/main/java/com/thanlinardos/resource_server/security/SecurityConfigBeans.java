package com.thanlinardos.resource_server.security;

import com.thanlinardos.resource_server.service.KeycloakMappingService;
import com.thanlinardos.resource_server.service.OwnerService;
import com.thanlinardos.resource_server.service.userservice.KeycloakUserService;
import com.thanlinardos.resource_server.service.userservice.OAuth2ServerUserService;
import com.thanlinardos.resource_server.service.userservice.api.UserService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types.OAuth2AuthServerType;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Component;

import java.net.URI;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants.SecurityCommonConstants.ROLE_PREFIX;

@Component
public class SecurityConfigBeans {

    @Value("${oauth2.github.client.id}")
    private String githubClientId;
    @Value("${oauth2.github.client.secret}")
    private String githubClientSecret;
    @Value("${oauth2.facebook.client.id}")
    private String facebookClientId;
    @Value("${oauth2.facebook.client.secret}")
    private String facebookClientSecret;
    @Value("${oauth2.auth-server}")
    private OAuth2AuthServerType authServer;
    @Value("${oauth2.keycloak.realm}")
    private String realmName;
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    @RefreshScope
    public UserService userService(OwnerService ownerService, @Autowired(required = false) Keycloak keycloak, KeycloakMappingService keycloakMappingService) {
        switch (authServer) {
            case KEYCLOAK -> {
                return new KeycloakUserService(ownerService, keycloak, realmName, keycloakMappingService);
            }
            case SPRING_OAUTH2_SERVER -> {
                return new OAuth2ServerUserService(ownerService, URI.create(issuerUri).getHost());
            }
            default -> throw new IllegalArgumentException("Unsupported auth server type: " + authServer);
        }
    }

    // Without this the default implementation of sessionDestroyed is NO-OP and therefore would
    // not publish a HttpSessionDestroyedEvent to notify the SessionRegistryImpl#onApplicationEvent
    // that the session has been evicted.
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(ROLE_PREFIX);   // this is the default role prefix
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration github = githubClientRegistration();
        ClientRegistration facebook = facebookClientRegistration();
        return new InMemoryClientRegistrationRepository(github, facebook);
    }

    private ClientRegistration githubClientRegistration() {
        return CommonOAuth2Provider.GITHUB.getBuilder("github")
                .clientId(githubClientId)
                .clientSecret(githubClientSecret)
                .build();
    }

    private ClientRegistration facebookClientRegistration() {
        return CommonOAuth2Provider.FACEBOOK.getBuilder("facebook")
                .clientId(facebookClientId)
                .clientSecret(facebookClientSecret)
                .build();
    }
}

package com.thanlinardos.resource_server.security.keycloak;

import com.thanlinardos.spring_enterprise_library.model.properties.KeyAndTrustStoreProperties;
import com.thanlinardos.resource_server.model.properties.keycloak.KeycloakClientProperties;
import com.thanlinardos.resource_server.model.properties.keycloak.KeycloakProperties;
import com.thanlinardos.spring_enterprise_library.https.SecureHttpRequestFactory;
import com.thanlinardos.spring_enterprise_library.https.SslContextUtil;
import jakarta.ws.rs.client.ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Configuration
@ConditionalOnExpression("'${integration.test.enabled}'=='false' && '${oauth2.auth-server}' == 'KEYCLOAK'")
@Slf4j
public class KeycloakAdminConfig {

    @Bean
    @RefreshScope
    public KeycloakProperties keycloakProperties(@Value("${oauth2.keycloak.url}") String url,
                                                 @Value("${oauth2.keycloak.realm}") String realm,
                                                 @Value("${oauth2.keycloak.client.id}") String clientId,
                                                 @Value("${oauth2.keycloak.client.secret}") String clientSecret,
                                                 @Value("${oauth2.keycloak.client.keystore.path}") Resource keystorePath,
                                                 @Value("${oauth2.keycloak.client.keystore.password}") String keystorePassword,
                                                 @Value("${oauth2.keycloak.client.truststore.path}") Resource truststorePath,
                                                 @Value("${oauth2.keycloak.client.truststore.password}") String truststorePassword) {
        KeyAndTrustStoreProperties keystore = new KeyAndTrustStoreProperties(keystorePath, keystorePassword);
        KeyAndTrustStoreProperties truststore = new KeyAndTrustStoreProperties(truststorePath, truststorePassword);
        KeycloakClientProperties clientProperties = new KeycloakClientProperties(clientId, clientSecret, keystore, truststore);
        return new KeycloakProperties(url, realm, clientProperties);
    }

    @Bean
    @RefreshScope
    Keycloak keycloak(KeycloakProperties keycloakProperties) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, CertificateException, UnrecoverableKeyException {
        log.info("Building Keycloak admin client");
        KeycloakClientProperties clientProperties = keycloakProperties.getClient();
        SSLContext sslContext = SslContextUtil.buildSSLContext(clientProperties.getKeystore(), clientProperties.getTruststore());
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getUrl())
                .realm(keycloakProperties.getRealm())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientProperties.getId())
                .clientSecret(clientProperties.getSecret())
                .resteasyClient(ClientBuilder.newBuilder()
                        .sslContext(sslContext)
                        .build())
                .build();
    }

    @Bean
    @RefreshScope
    public JwtDecoder jwtDecoder(OAuth2ResourceServerProperties oAuth2ResourceServerProperties,
                                 KeycloakProperties keycloakProperties
    ) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException {
        KeycloakClientProperties clientProperties = keycloakProperties.getClient();
        SSLContext sslContext = SslContextUtil.buildSSLContext(clientProperties.getKeystore(), clientProperties.getTruststore());
        SecureHttpRequestFactory requestFactory = new SecureHttpRequestFactory(sslContext);
        return NimbusJwtDecoder
                .withIssuerLocation(oAuth2ResourceServerProperties.getJwt().getIssuerUri())
                .restOperations(new RestTemplate(requestFactory))
                .build();
    }
}

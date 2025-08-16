package com.thanlinardos.resource_server.model.properties.keycloak;


import com.thanlinardos.spring_enterprise_library.model.properties.KeyAndTrustStoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeycloakClientProperties {

    private String id;
    private String secret;
    private KeyAndTrustStoreProperties keystore;
    private KeyAndTrustStoreProperties truststore;
}

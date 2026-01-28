package com.thanlinardos.resource_server.model.properties.keycloak;

import com.thanlinardos.spring_enterprise_library.https.properties.KeyAndTrustStoreProperties;

public record KeycloakClientProperties(String id, String secret, KeyAndTrustStoreProperties keystore, KeyAndTrustStoreProperties truststore) {
}

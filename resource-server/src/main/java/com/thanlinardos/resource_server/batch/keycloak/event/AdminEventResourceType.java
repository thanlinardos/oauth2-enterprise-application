package com.thanlinardos.resource_server.batch.keycloak.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AdminEventResourceType {

    AUTH_EXECUTION("AUTH_EXECUTION"),
    AUTH_EXECUTION_FLOW("AUTH_EXECUTION_FLOW"),
    AUTH_FLOW("AUTH_FLOW"),
    AUTHENTICATOR_CONFIG("AUTHENTICATOR_CONFIG"),
    AUTHORIZATION_POLICY("AUTHORIZATION_POLICY"),
    AUTHORIZATION_RESOURCE("AUTHORIZATION_RESOURCE"),
    AUTHORIZATION_RESOURCE_SERVER("AUTHORIZATION_RESOURCE_SERVER"),
    AUTHORIZATION_SCOPE("AUTHORIZATION_SCOPE"),
    CLIENT("CLIENT"),
    CLIENT_INITIAL_ACCESS_MODEL("CLIENT_INITIAL_ACCESS_MODEL"),
    CLIENT_ROLE("CLIENT_ROLE"),
    CLIENT_ROLE_MAPPING("CLIENT_ROLE_MAPPING"),
    CLIENT_SCOPE("CLIENT_SCOPE"),
    CLIENT_SCOPE_CLIENT_MAPPING("CLIENT_SCOPE_CLIENT_MAPPING"),
    CLIENT_SCOPE_MAPPING("CLIENT_SCOPE_MAPPING"),
    CLUSTER_NODE("CLUSTER_NODE"),
    COMPONENT("COMPONENT"),
    CUSTOM("CUSTOM"),
    GROUP("GROUP"),
    GROUP_MEMBERSHIP("GROUP_MEMBERSHIP"),
    IDENTITY_PROVIDER("IDENTITY_PROVIDER"),
    IDENTITY_PROVIDER_MAPPER("IDENTITY_PROVIDER_MAPPER"),
    PROTOCOL_MAPPER("PROTOCOL_MAPPER"),
    REALM("REALM"),
    REALM_ROLE("REALM_ROLE"),
    REALM_ROLE_MAPPING("REALM_ROLE_MAPPING"),
    REALM_SCOPE_MAPPING("REALM_SCOPE_MAPPING"),
    REQUIRED_ACTION("REQUIRED_ACTION"),
    USER("USER"),
    USER_FEDERATION_MAPPER("USER_FEDERATION_MAPPER"),
    USER_FEDERATION_PROVIDER("USER_FEDERATION_PROVIDER"),
    USER_LOGIN_FAILURE("USER_LOGIN_FAILURE"),
    USER_SESSION("USER_SESSION");

    private final String value;

    AdminEventResourceType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static AdminEventResourceType fromValue(String value) {
        for (AdminEventResourceType b : AdminEventResourceType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

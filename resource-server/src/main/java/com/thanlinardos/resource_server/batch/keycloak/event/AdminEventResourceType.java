package com.thanlinardos.resource_server.batch.keycloak.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AdminEventResourceType {

    AUTH_EXECUTION("AUTH_EXECUTION", false),
    AUTH_EXECUTION_FLOW("AUTH_EXECUTION_FLOW", false),
    AUTH_FLOW("AUTH_FLOW", false),
    AUTHENTICATOR_CONFIG("AUTHENTICATOR_CONFIG", false),
    AUTHORIZATION_POLICY("AUTHORIZATION_POLICY", false),
    AUTHORIZATION_RESOURCE("AUTHORIZATION_RESOURCE", false),
    AUTHORIZATION_RESOURCE_SERVER("AUTHORIZATION_RESOURCE_SERVER", false),
    AUTHORIZATION_SCOPE("AUTHORIZATION_SCOPE", false),
    CLIENT("CLIENT", true),
    CLIENT_INITIAL_ACCESS_MODEL("CLIENT_INITIAL_ACCESS_MODEL", false),
    CLIENT_ROLE("CLIENT_ROLE", false),
    CLIENT_ROLE_MAPPING("CLIENT_ROLE_MAPPING", false),
    CLIENT_SCOPE("CLIENT_SCOPE", false),
    CLIENT_SCOPE_CLIENT_MAPPING("CLIENT_SCOPE_CLIENT_MAPPING", false),
    CLIENT_SCOPE_MAPPING("CLIENT_SCOPE_MAPPING", false),
    CLUSTER_NODE("CLUSTER_NODE", false),
    COMPONENT("COMPONENT", false),
    CUSTOM("CUSTOM", false),
    GROUP("GROUP", false),
    GROUP_MEMBERSHIP("GROUP_MEMBERSHIP", false),
    IDENTITY_PROVIDER("IDENTITY_PROVIDER", false),
    IDENTITY_PROVIDER_MAPPER("IDENTITY_PROVIDER_MAPPER", false),
    PROTOCOL_MAPPER("PROTOCOL_MAPPER", false),
    REALM("REALM", false),
    REALM_ROLE("REALM_ROLE", false),
    REALM_ROLE_MAPPING("REALM_ROLE_MAPPING", true),
    REALM_SCOPE_MAPPING("REALM_SCOPE_MAPPING", false),
    REQUIRED_ACTION("REQUIRED_ACTION", false),
    USER("USER", true),
    USER_FEDERATION_MAPPER("USER_FEDERATION_MAPPER", false),
    USER_FEDERATION_PROVIDER("USER_FEDERATION_PROVIDER", false),
    USER_LOGIN_FAILURE("USER_LOGIN_FAILURE", false),
    USER_SESSION("USER_SESSION", false);

    private final String value;
    private final boolean hasResourceId;

    AdminEventResourceType(String value, boolean hasResourceId) {
        this.value = value;
        this.hasResourceId = hasResourceId;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonIgnore
    public boolean hasResourceId() {
        return hasResourceId;
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

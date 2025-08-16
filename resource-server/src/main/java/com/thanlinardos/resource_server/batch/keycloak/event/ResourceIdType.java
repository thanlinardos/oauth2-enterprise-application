package com.thanlinardos.resource_server.batch.keycloak.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResourceIdType {

    CLIENTS("clients"),
    USERS("users"),
    EVENTS("events");

    private final String value;

    ResourceIdType(String value) {
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
    public static ResourceIdType fromValue(String value) {
        for (ResourceIdType b : ResourceIdType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

package com.thanlinardos.resource_server.batch.keycloak.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AdminEventOperationType {

    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    ACTION("ACTION");

    private final String value;

    AdminEventOperationType(String value) {
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
    public static AdminEventOperationType fromValue(String value) {
        for (AdminEventOperationType b : AdminEventOperationType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

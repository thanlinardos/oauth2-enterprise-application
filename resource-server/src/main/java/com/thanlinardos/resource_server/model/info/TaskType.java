package com.thanlinardos.resource_server.model.info;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskType {

    KEYCLOAK_EVENT_TASK("KEYCLOAK_EVENT_TASK"),
    KEYCLOAK_ADMIN_EVENT_TASK("KEYCLOAK_ADMIN_EVENT_TASK");

    private final String value;

    TaskType(String value) {
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
    public static TaskType fromValue(String value) {
        for (TaskType b : TaskType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

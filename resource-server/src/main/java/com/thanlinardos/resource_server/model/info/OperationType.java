package com.thanlinardos.resource_server.model.info;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OperationType {

    CREATE("CREATE"),
    ADD("ADD"),
    VIEW("VIEW"),
    UPDATE("UPDATE"),
    DELETE("DELETE");

    private final String value;

    OperationType(String value) {
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
    public static OperationType fromValue(String value) {
        for (OperationType b : OperationType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

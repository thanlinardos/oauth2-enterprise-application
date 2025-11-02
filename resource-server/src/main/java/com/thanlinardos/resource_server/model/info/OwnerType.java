package com.thanlinardos.resource_server.model.info;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OwnerType {

    CUSTOMER("CUSTOMER"),
    CLIENT("CLIENT");

    private final String value;

    OwnerType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OwnerType fromValue(String value) {
        for (OwnerType o : OwnerType.values()) {
            if (o.value.equals(value)) {
                return o;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

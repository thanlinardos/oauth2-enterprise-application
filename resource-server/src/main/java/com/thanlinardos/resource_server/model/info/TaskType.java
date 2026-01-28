package com.thanlinardos.resource_server.model.info;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;

public enum TaskType {

    KEYCLOAK_EVENT_TASK("KEYCLOAK_EVENT_TASK");

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
        for (TaskType type : TaskType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw ErrorCode.ILLEGAL_ARGUMENT.createCoreException("Unexpected value: {0} for TaskType enum.", new Object[]{value});
    }
}

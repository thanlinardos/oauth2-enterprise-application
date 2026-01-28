package com.thanlinardos.resource_server.batch.keycloak.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum EventStatusType {

    RECEIVED("RECEIVED", false),
    PROCESSED("PROCESSED", false),
    IGNORED("IGNORED", false),
    FAILED("FAILED", true),
    SKIPPED_AS_FAILED("SKIPPED_AS_FAILED", true);

    private final String value;
    @Getter
    private final boolean failed;

    EventStatusType(String value, boolean failed) {
        this.value = value;
        this.failed = failed;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static Set<EventStatusType> getFailedStatuses() {
        return Arrays.stream(values())
                .filter(EventStatusType::isFailed)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static EventStatusType fromValue(String value) {
        for (EventStatusType b : EventStatusType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

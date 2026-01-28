package com.thanlinardos.resource_server.model.entity.keycloak.converter;

import com.thanlinardos.resource_server.batch.keycloak.event.EventStatusType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.annotation.Nullable;

@Converter(autoApply = true)
public class EventStatusConverter implements AttributeConverter<EventStatusType, String> {

    public EventStatusConverter() {
        // Default constructor
    }

    @Override
    public String convertToDatabaseColumn(@Nullable final EventStatusType status) {
        return status == null ? null : status.name();
    }

    @Override
    public EventStatusType convertToEntityAttribute(@Nullable final String statusString) {
        return statusString == null ? null : EventStatusType.fromValue(statusString);
    }
}

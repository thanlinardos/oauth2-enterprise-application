package com.thanlinardos.resource_server.model.entity.keycloak.converter;

import com.thanlinardos.resource_server.batch.keycloak.event.AdminEventOperationType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.annotation.Nullable;

@Converter(autoApply = true)
public class AdminEventOperationTypeConverter implements AttributeConverter<AdminEventOperationType, String> {

    public AdminEventOperationTypeConverter() {
        // Default constructor
    }

    @Override
    public String convertToDatabaseColumn(@Nullable final AdminEventOperationType type) {
        return type == null ? null : type.name();
    }

    @Override
    public AdminEventOperationType convertToEntityAttribute(@Nullable final String typeString) {
        return typeString == null ? null : AdminEventOperationType.fromValue(typeString);
    }
}

package com.thanlinardos.resource_server.model.entity.keycloak.converter;

import com.thanlinardos.resource_server.batch.keycloak.event.AdminEventResourceType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.annotation.Nullable;

@Converter(autoApply = true)
public class AdminEventResourceTypeConverter implements AttributeConverter<AdminEventResourceType, String> {

    public AdminEventResourceTypeConverter() {
        // Default constructor
    }

    @Override
    public String convertToDatabaseColumn(@Nullable final AdminEventResourceType type) {
        return type == null ? null : type.name();
    }

    @Override
    public AdminEventResourceType convertToEntityAttribute(@Nullable final String typeString) {
        return typeString == null ? null : AdminEventResourceType.fromValue(typeString);
    }
}

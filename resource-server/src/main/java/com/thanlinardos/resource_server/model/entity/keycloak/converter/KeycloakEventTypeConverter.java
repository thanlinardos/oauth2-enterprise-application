package com.thanlinardos.resource_server.model.entity.keycloak.converter;

import com.thanlinardos.resource_server.batch.keycloak.event.KeycloakUserEventType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.annotation.Nullable;

@Converter(autoApply = true)
public class KeycloakEventTypeConverter implements AttributeConverter<KeycloakUserEventType, String> {

    public KeycloakEventTypeConverter() {
        // Default constructor
    }

    @Override
    public String convertToDatabaseColumn(@Nullable final KeycloakUserEventType type) {
        return type == null ? null : type.name();
    }

    @Override
    public KeycloakUserEventType convertToEntityAttribute(@Nullable final String typeString) {
        return typeString == null ? null : KeycloakUserEventType.fromValue(typeString);
    }
}

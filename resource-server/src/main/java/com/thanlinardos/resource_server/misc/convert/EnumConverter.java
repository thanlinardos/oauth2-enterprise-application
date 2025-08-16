package com.thanlinardos.resource_server.misc.convert;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EnumConverter implements AttributeConverter<Enum<?>, String> {


    @Override
    public String convertToDatabaseColumn(Enum<?> attribute) {
        return attribute.name();
    }

    @Override
    public Enum<?> convertToEntityAttribute(String dbData) {
        return Enum.valueOf(Enum.class, dbData);
    }
}

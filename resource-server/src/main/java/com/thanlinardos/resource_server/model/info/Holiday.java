package com.thanlinardos.resource_server.model.info;

public record Holiday(String day, String reason, Type type) {

    public enum Type {
        FESTIVAL, FEDERAL
    }
}

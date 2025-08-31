package com.thanlinardos.cloud_config_server.vault.properties.update_credentials;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record ApplicationEnvironmentProperties(@JsonProperty("name") String name, @JsonProperty("role") String role) implements Serializable {
}

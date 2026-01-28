package com.thanlinardos.cloud_config_server.vault.properties.update.credentials;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public record ApplicationVaultProperties(@JsonProperty("name") String name,
                                         @JsonProperty("environments") List<ApplicationEnvironmentProperties> environments) implements Serializable {
}

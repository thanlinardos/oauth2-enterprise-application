package com.thanlinardos.cloud_config_server.vault.properties.update.credentials;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@JsonTypeName("update-vault-db-credentials")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public record VaultUpdateCredentialsProperties(
        @JsonProperty("applications") List<ApplicationVaultProperties> applications,
        @JsonProperty("environments-to-sync") List<String> environmentsToSync,
        @JsonProperty("sync-all-environments") boolean syncAllEnvironments,
        @JsonProperty("update-configurations-interval-in-seconds") long updateConfigsInterval) implements Serializable {

    @JsonIgnore
    public boolean isEnvironmentToSync(ApplicationEnvironmentProperties environment) {
        return syncAllEnvironments || environmentsToSync.contains(environment.name());
    }

    @JsonIgnore
    public Instant getNextUpdateConfigsInstant(Instant now) {
        return now.plusSeconds(updateConfigsInterval);
    }
}

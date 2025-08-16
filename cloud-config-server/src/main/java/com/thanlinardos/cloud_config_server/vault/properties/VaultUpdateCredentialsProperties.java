package com.thanlinardos.cloud_config_server.vault.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.Serializable;
import java.util.List;

@JsonTypeName("update-vault-db-credentials")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public record VaultUpdateCredentialsProperties(@JsonProperty("applications") List<ApplicationVaultProperties> applications,
                                               @JsonProperty("environments-to-sync") List<String> environmentsToSync,
                                               @JsonProperty("sync-all-environments") boolean syncAllEnvironments,
                                               @JsonProperty("update-configurations-interval-in-seconds") long updateConfigsInterval)  implements Serializable {

}

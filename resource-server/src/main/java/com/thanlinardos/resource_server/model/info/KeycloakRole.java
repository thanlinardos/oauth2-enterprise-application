package com.thanlinardos.resource_server.model.info;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class KeycloakRole implements Serializable {

    @NotNull
    private UUID id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @Builder.Default
    private boolean composite = false;
    @Builder.Default
    private boolean clientRole = false;
    @NotNull
    private UUID containerId;
}

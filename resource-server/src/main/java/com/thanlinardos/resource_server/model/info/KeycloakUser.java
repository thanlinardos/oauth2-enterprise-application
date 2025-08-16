package com.thanlinardos.resource_server.model.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class KeycloakUser implements Serializable {

    @NotNull
    private UUID id;
    @NotBlank
    private String username;
    @NotBlank @Email
    private String email;
    @Builder.Default
    private boolean emailVerified = false;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Builder.Default
    private boolean enabled = false;
    @JsonProperty("totp")
    @Builder.Default
    private boolean timeBasedOneTimePassword = false;
    @Builder.Default
    private List<@NotBlank String> disableableCredentialTypes = new ArrayList<>();
    @Builder.Default
    private List<@NotBlank String> requiredActions = new ArrayList<>();
    @NotNull
    private Integer notBefore;
    @NotNull
    private KeycloakUserAccess access;
}

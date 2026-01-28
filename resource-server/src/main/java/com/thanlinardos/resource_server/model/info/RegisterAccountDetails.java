package com.thanlinardos.resource_server.model.info;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record RegisterAccountDetails(@NotBlank String username, long accountNumber, @NotBlank String accountType, @NotBlank String branchAddress) implements Serializable {
}

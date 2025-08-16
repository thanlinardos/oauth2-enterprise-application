package com.thanlinardos.resource_server.model.mapped;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginRequestModel {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
}

package com.thanlinardos.resource_server.model.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.ToString;

import java.io.Serializable;

public record RegisterCustomerDetails(@NotBlank
                                      @Email
                                      String email,
                                      @NotNull
                                      @Digits(integer = 10, fraction = 0)
                                      String mobileNumber,
                                      String firstName,
                                      String lastName,
                                      @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                                      @ToString.Exclude
                                      @NotBlank
                                      String password) implements Serializable {
}

package com.thanlinardos.resource_server.model.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RegisterCustomerDetails implements Serializable {

    @NotBlank
    @Email
    private String email;
    @NotNull
    @Digits(integer = 10, fraction = 0)
    private String mobileNumber;
    private String firstName;
    private String lastName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    @NotBlank
    private String password;

    private RegisterCustomerDetails() {
    }
}

package com.thanlinardos.resource_server.model.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
public class Customer extends OwnerDetailsInfo implements Serializable {

    @Email
    @NotBlank
    private String email;
    @Digits(integer = 10, fraction = 0)
    private String mobileNumber;
    private String firstName;
    private String lastName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    @NotBlank
    private String password;

    public RegisterCustomerDetails toRegisterCustomerDetails() {
        return new RegisterCustomerDetails(email, mobileNumber, firstName, lastName, password);
    }

    @Override
    public String getPrincipalName() {
        return email;
    }
}

package com.thanlinardos.resource_server.model.mapped;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponseModel {

    private String status;
    private String jwtToken;
    private Boolean isCompromised;
}

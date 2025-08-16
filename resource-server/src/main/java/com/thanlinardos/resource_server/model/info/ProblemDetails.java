package com.thanlinardos.resource_server.model.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemDetails {

    private String title;
    private int status;
    private String detail;
    private String type;
    private String cause;
}

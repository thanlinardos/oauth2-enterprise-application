package com.thanlinardos.resource_server.model.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactMessage implements Serializable {

    private String contactId;
    private String contactName;
    private String contactEmail;
    private String subject;
    private String message;
    private LocalDate createDt;
}

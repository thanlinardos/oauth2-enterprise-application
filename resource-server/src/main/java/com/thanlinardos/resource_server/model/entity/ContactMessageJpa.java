package com.thanlinardos.resource_server.model.entity;

import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import com.thanlinardos.resource_server.model.info.ContactMessage;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "contact_messages")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class ContactMessageJpa extends BasicIdJpa {

    private String contactId;
    private String contactName;
    private String contactEmail;
    private String subject;
    private String message;

    public static ContactMessageJpa fromInfo(ContactMessage info) {
        return builder()
                .contactId(info.getContactId())
                .contactName(info.getContactName())
                .contactEmail(info.getContactEmail())
                .subject(info.getSubject())
                .message(info.getMessage())
                .build();
    }
}

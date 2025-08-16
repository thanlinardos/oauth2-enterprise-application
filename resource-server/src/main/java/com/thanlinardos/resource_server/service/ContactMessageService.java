package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.model.entity.ContactMessageJpa;
import com.thanlinardos.resource_server.model.info.ContactMessage;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final EntityManager entityManager;

    @Transactional
    public void saveContactMessage(ContactMessage contactMessage) {
        ContactMessageJpa entity = ContactMessageJpa.fromInfo(contactMessage);
        entityManager.persist(entity);
    }
}

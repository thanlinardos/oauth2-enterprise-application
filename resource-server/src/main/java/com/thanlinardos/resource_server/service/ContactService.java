package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.model.entity.ContactMessageJpa;
import com.thanlinardos.resource_server.model.info.Contact;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;

import java.security.SecureRandom;
import java.util.Random;

import static com.thanlinardos.resource_server.controller.rest.ContactController.getServiceReqNumber;

@Slf4j
@Service
// @RequestScope
// @SessionScope
@ApplicationScope
@RequiredArgsConstructor
public class ContactService {

    private final EntityManager entityManager;
    private final Random random = new SecureRandom();

    @Getter
    @Setter
    private int counter = 0;

    /**
     * Save Contact Details into DB
     *
     * @param contact Contact
     */
    @Transactional
    public void saveMessageDetails(Contact contact) {
        ContactMessageJpa contactMessageJpa = new ContactMessageJpa();
        contactMessageJpa.setContactEmail(contact.getEmail());
        contactMessageJpa.setMessage(contact.getMessage());
        contactMessageJpa.setContactName(contact.getName());
        contactMessageJpa.setSubject(contact.getSubject());
        contactMessageJpa.setContactId(getServiceReqNumber(random));
        entityManager.persist(contactMessageJpa);
    }
}
package com.thanlinardos.resource_server.service.contact;

import com.thanlinardos.resource_server.model.entity.contact.ContactMessageJpa;
import com.thanlinardos.resource_server.model.info.ContactMessage;
import com.thanlinardos.resource_server.repository.api.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;

    @Transactional
    public void saveContactMessage(ContactMessage contactMessage) {
        ContactMessageJpa entity = ContactMessageJpa.fromInfo(contactMessage);
        contactMessageRepository.save(entity);
    }
}

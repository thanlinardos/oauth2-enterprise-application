package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.contact.ContactMessageJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessageJpa, Long> {
}

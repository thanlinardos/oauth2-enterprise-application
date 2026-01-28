package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.owner.ClientJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientJpa, Long> {

    Optional<ClientJpa> getFirstByName(String name);
}

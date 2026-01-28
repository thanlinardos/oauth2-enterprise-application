package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.batch.keycloak.event.EventStatusType;
import com.thanlinardos.resource_server.model.entity.keycloak.KeycloakEventJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface KeycloakEventRepository extends JpaRepository<KeycloakEventJpa, Long>, CustomKeycloakEventRepository {

    List<KeycloakEventJpa> findAllByStatusIn(Collection<EventStatusType> statuses);
}

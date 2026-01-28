package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.batch.keycloak.event.EventStatusType;
import com.thanlinardos.resource_server.model.entity.keycloak.KeycloakAdminEventJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface KeycloakAdminEventRepository extends JpaRepository<KeycloakAdminEventJpa, Long>, CustomKeycloakAdminEventRepository {

    List<KeycloakAdminEventJpa> findAllByStatusIn(Collection<EventStatusType> statuses);
}

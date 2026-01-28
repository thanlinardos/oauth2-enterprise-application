package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.owner.OwnerJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository extends JpaRepository<OwnerJpa, Long>, CustomOwnerRepository {

    Optional<OwnerJpa> getFirstByName(String name);

    Optional<OwnerJpa> getFirstByClient_ServiceAccountId(UUID serviceAccountId);

    Optional<OwnerJpa> getFirstByCustomer_Username(String username);

    boolean existsByClient_ServiceAccountId(UUID serviceAccountId);

    boolean existsByUuid(UUID uuid);

    boolean existsByName(String name);

    Optional<OwnerJpa> getFirstByUuid(UUID uuid);
}

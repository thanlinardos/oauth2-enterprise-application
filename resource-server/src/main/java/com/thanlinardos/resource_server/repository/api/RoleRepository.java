package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.role.RoleJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleJpa, Long> {

    Optional<RoleJpa> findByRole(String role);
}

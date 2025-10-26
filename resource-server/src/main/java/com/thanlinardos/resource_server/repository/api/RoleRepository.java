package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.RoleJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleJpa, Long> {
}

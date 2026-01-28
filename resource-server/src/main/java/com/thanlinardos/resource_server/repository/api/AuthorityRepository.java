package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.role.AuthorityJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<AuthorityJpa, Long> {
}

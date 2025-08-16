package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.model.entity.AuthorityJpa;
import com.thanlinardos.resource_server.model.entity.RoleJpa;
import com.thanlinardos.resource_server.model.mapped.AuthorityModel;
import com.thanlinardos.resource_server.model.mapped.RoleModel;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Authority;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleCacheService {

    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    @Cacheable(value = "roles")
    public Collection<RoleModel> getAllRoles() {
        return entityManager.createQuery("from RoleJpa r join fetch r.authorities", RoleJpa.class)
                .getResultStream()
                .map(RoleModel::new)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "authorities")
    public List<Authority> getAllAuthorities() {
        return entityManager.createQuery("from AuthorityJpa", AuthorityJpa.class).getResultStream()
                .map(AuthorityModel::new)
                .map(Authority.class::cast)
                .toList();
    }
}

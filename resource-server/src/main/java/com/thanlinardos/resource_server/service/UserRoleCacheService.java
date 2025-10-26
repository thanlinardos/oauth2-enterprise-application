package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.model.mapped.AuthorityModel;
import com.thanlinardos.resource_server.model.mapped.RoleModel;
import com.thanlinardos.resource_server.repository.api.AuthorityRepository;
import com.thanlinardos.resource_server.repository.api.RoleRepository;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleCacheService {

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "roles")
    public Collection<RoleModel> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleModel::new)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "authorities")
    public List<Authority> getAllAuthorities() {
        return authorityRepository.findAll().stream()
                .map(AuthorityModel::new)
                .map(Authority.class::cast)
                .toList();
    }
}

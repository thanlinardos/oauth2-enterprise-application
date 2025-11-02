package com.thanlinardos.resource_server.service.roleservice;

import com.thanlinardos.resource_server.model.mapped.RoleModel;
import com.thanlinardos.resource_server.service.UserRoleCacheService;
import com.thanlinardos.resource_server.service.roleservice.api.OauthRoleService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements OauthRoleService {

    private final UserRoleCacheService userRoleCacheService;

    @Override
    public Collection<RoleModel> getAllRoles() {
        return userRoleCacheService.getAllRoles();
    }

    @Override
    public RoleModel findRole(String name) {
        return getAllRoles().stream()
                .filter(role -> role.getRole().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Collection<RoleModel> findRoles(Collection<String> names) {
        return getAllRoles().stream()
                .filter(role -> names.contains(role.getName()))
                .toList();
    }

    @Override
    public int getPrivilegeLevelFromRoleNames(Collection<String> names) {
        return getAllRoles().stream()
                .filter(role -> names.contains(role.getName()))
                .map(RoleModel::getPrivilegeLvl)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
    }

    @Override
    public Collection<GrantedAuthority> findGrantedAuthoritiesWithRole(RoleModel role) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(role.getGrantedAuthorities());
        grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        return grantedAuthorities;
    }

    @Override
    public Collection<GrantedAuthority> findGrantedAuthoritiesWithRoles(Collection<String> roleNames) {
        return getAllRoles().stream()
                .filter(role -> roleNames.contains(role.getName()))
                .map(this::findGrantedAuthoritiesWithRole)
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    public Collection<Authority> getAllAuthorities() {
        return userRoleCacheService.getAllAuthorities();
    }
}

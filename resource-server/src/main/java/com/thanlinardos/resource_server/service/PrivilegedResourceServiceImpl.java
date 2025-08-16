package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.misc.utils.RoleUtils;
import com.thanlinardos.resource_server.service.roleservice.RoleServiceImpl;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.api.service.PrivilegedResourceService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.PrivilegedResource;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RequiredArgsConstructor
@Service
public class PrivilegedResourceServiceImpl implements PrivilegedResourceService {

    private final RoleServiceImpl roleService;

    /**
     * Check if the currently authenticated principal inside {@link Authentication} can access the resource.
     * The principal can access the resource if the principal's privilege level is less than or equal to the resource's maximum privilege level
     * and if one of these 2 conditions is true:
     * - the principal's privilege level is less than the resource's privilege level.
     * - the principal's privilege level is equal to the resource's privilege level and the principal is the owner of the resource.
     *
     * @param resource       - the resource to access
     * @param authentication - the authentication to check
     * @return true if the principal can access the resource, false otherwise
     */
    private boolean canAccessResource(PrivilegedResource resource, Authentication authentication) {
        if (resource == null) {
            return false;
        }
        Integer privilegeLevel = getPrivilegeLevelFromAuthorities(authentication.getAuthorities());
        Jwt principal = (Jwt) authentication.getPrincipal();
        return privilegeLevel <= resource.getMaxPrivilegeLevel() &&
                (privilegeLevel < resource.getPrivilegeLevel() ||
                        (privilegeLevel.equals(resource.getPrivilegeLevel()) && resource.samePrivilegeLevelCheck(principal))
                );
    }

    @Override
    public boolean canCurrentPrincipalAccessResource(PrivilegedResource resource) {
        Authentication authentication = getContext().getAuthentication();
        if (!validateOAuth2Authentication(authentication)) {
            return false;
        }
        return canAccessResource(resource, authentication);
    }

    private boolean validateOAuth2Authentication(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities() != null
                && !authentication.getAuthorities().isEmpty()
                && authentication.getAuthorities().stream().allMatch(Objects::nonNull)
                && authentication.getPrincipal() != null
                && authentication.getPrincipal() instanceof Jwt jwt
                && jwt.getClaims() != null
                && jwt.getClaims().containsKey("resource_access")
                && jwt.getClaims().get("resource_access") != null
                && StringUtils.isNotBlank(jwt.getSubject());
    }

    private Integer getPrivilegeLevelFromAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Collection<String> roleNames = RoleUtils.getRoleNamesFromAuthorities(authorities);
        return roleService.getPrivilegeLevelFromRoleNames(roleNames);
    }
}

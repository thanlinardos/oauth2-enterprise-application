package com.thanlinardos.resource_server.misc.utils;

import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.security.core.GrantedAuthority;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants.SecurityCommonConstants.ROLE_PREFIX;

public class RoleUtils {

    private RoleUtils() {
    }

    public static List<String> getRoleNamesFromRoleRepresentations(List<RoleRepresentation> roleRepresentations) {
        return roleRepresentations.stream()
                .map(RoleRepresentation::getName)
                .map(roleName -> ROLE_PREFIX + roleName)
                .toList();
    }

    public static @Nonnull List<String> getRoleNamesFromAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(name -> name.startsWith(ROLE_PREFIX))
                .toList();
    }
}

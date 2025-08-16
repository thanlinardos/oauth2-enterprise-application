package com.thanlinardos.resource_server.model.mapped;

import com.thanlinardos.resource_server.model.entity.RoleJpa;
import com.thanlinardos.resource_server.model.mapped.base.BasicIdModel;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Authority;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Role;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types.AccessType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants.SecurityCommonConstants.ROLE_PREFIX;

@AllArgsConstructor
@SuperBuilder
@ToString
@EqualsAndHashCode(callSuper = true)
@Getter
public class RoleModel extends BasicIdModel implements Serializable, Role {

    private String role;
    private int privilegeLvl;
    private List<AuthorityModel> authorities;

    public RoleModel(RoleJpa entity) {
        super(entity);
        this.role = entity.getRole();
        this.privilegeLvl = entity.getPrivilegeLvl();
        this.authorities = entity.getAuthorities().stream()
                .map(AuthorityModel::new)
                .toList();
    }

    @Override
    public Collection<GrantedAuthority> getGrantedAuthorities() {
        return authorities.stream()
                .map(Authority::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return role;
    }

    public String getNameNoPrefix() {
        return role.startsWith(ROLE_PREFIX) ? role.substring(ROLE_PREFIX.length()) : role;
    }
}

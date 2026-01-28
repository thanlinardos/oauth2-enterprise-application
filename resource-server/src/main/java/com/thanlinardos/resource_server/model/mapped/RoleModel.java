package com.thanlinardos.resource_server.model.mapped;

import com.thanlinardos.resource_server.model.entity.role.RoleJpa;
import com.thanlinardos.spring_enterprise_library.model.mapped.base.BasicIdModel;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Authority;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants.SecurityCommonConstants.ROLE_PREFIX;

@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Getter
public class RoleModel extends BasicIdModel implements Serializable, Role {

    private String role;
    private int privilegeLvl;
    @Builder.Default
    private List<AuthorityModel> authorities = new ArrayList<>();

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
                .map(GrantedAuthority.class::cast)
                .toList();
    }

    @Override
    public String getName() {
        return role;
    }

    public String getNameNoPrefix() {
        return role.startsWith(ROLE_PREFIX) ? role.substring(ROLE_PREFIX.length()) : role;
    }

    @Override
    public String toString() {
        return "Role[name=" + role + ", lvl=" + privilegeLvl + "]";
    }
}

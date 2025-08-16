package com.thanlinardos.resource_server.model.entity;

import com.thanlinardos.resource_server.model.entity.base.BasicIdJpa;
import com.thanlinardos.resource_server.model.mapped.RoleModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@SuperBuilder
public class RoleJpa extends BasicIdJpa {

    @Column(length = 50, nullable = false, unique = true)
    private String role;

    @Column(nullable = false, columnDefinition = "int default 2147483647")
    private int privilegeLvl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "owner_roles",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "owner_id"))
    @ToString.Exclude
    @Builder.Default
    private List<OwnerJpa> owners = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_authorities",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"))
    @ToString.Exclude
    @Builder.Default
    private List<AuthorityJpa> authorities = new ArrayList<>();

    public static RoleJpa fromModel(RoleModel role) {
        return RoleJpa.builder()
                .id(role.getId())
                .role(role.getRole())
                .privilegeLvl(role.getPrivilegeLvl())
                .authorities(role.getAuthorities().stream()
                        .map(AuthorityJpa::fromModel)
                        .toList())
                .build();
    }

    public void addAuthorityWithLink(AuthorityJpa authority) {
        authorities.add(authority);
        authority.getRoles().add(this);
    }
}

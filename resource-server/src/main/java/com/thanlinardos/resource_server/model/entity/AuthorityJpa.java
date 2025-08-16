package com.thanlinardos.resource_server.model.entity;

import com.thanlinardos.resource_server.model.entity.base.BasicIdJpa;
import com.thanlinardos.resource_server.model.mapped.AuthorityModel;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types.AccessType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authorities")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AuthorityJpa extends BasicIdJpa {

    @Column(length = 50, nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private AccessType accessType;

    @Column(nullable = false)
    private String uri;

    @Nullable
    private String expression;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_authorities",
            joinColumns = @JoinColumn(name = "authority_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @ToString.Exclude
    @Builder.Default
    private List<RoleJpa> roles = new ArrayList<>();

    public static AuthorityJpa fromModel(AuthorityModel model) {
        return builder()
                .name(model.getName())
                .accessType(model.getAccess())
                .expression(model.getExpression())
                .build();
    }
}



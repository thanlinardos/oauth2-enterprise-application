package com.thanlinardos.resource_server.model.entity.base;

import com.thanlinardos.resource_server.model.entity.OwnerJpa;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public class BasicManyToOneOwnedIdJpa extends BasicIdJpa implements BasicOwnedEntity {

    @ManyToOne(targetEntity = OwnerJpa.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    private OwnerJpa owner;
}

package com.thanlinardos.resource_server.model.entity;

import com.thanlinardos.resource_server.model.entity.base.BasicAuditableJpa;
import com.thanlinardos.resource_server.model.mapped.OwnerModel;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.converters.UUIDConverter;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "owner")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class OwnerJpa extends BasicAuditableJpa {

    @Convert(converter = UUIDConverter.class)
    private UUID uuid;
    private String name;
    private String type;
    private int privilegeLevel;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @Nullable
    private CustomerJpa customer;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    @Nullable
    private ClientJpa client;

    @OneToOne(mappedBy = "owner")
    @ToString.Exclude
    private AccountJpa account;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "owner_roles",
            joinColumns = @JoinColumn(name = "owner_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private List<RoleJpa> roles = new ArrayList<>();

    public static OwnerJpa fromModel(OwnerModel owner) {
        OwnerJpa entity = builder()
                .id(owner.getId())
                .uuid(owner.getUuid())
                .name(owner.getPrincipalName())
                .type(owner.getType().toString())
                .privilegeLevel(owner.getPrivilegeLevel())
                .roles(owner.getRoles().stream()
                        .map(RoleJpa::fromModel)
                        .toList())
                .build();
        entity.setTrackedProperties(owner);
        if (owner.getCustomer() != null) {
            CustomerJpa customerJpa = CustomerJpa.fromModel(owner.getCustomer());
            customerJpa.setOwner(entity);
            entity.setCustomer(customerJpa);
        } else if (owner.getClient() != null) {
            ClientJpa clientJpa = ClientJpa.fromModel(owner.getClient());
            clientJpa.setOwner(entity);
            entity.setClient(clientJpa);
        }
        return entity;
    }

    public void setCustomerWithLink(CustomerJpa customer) {
        this.customer = customer;
        customer.setOwner(this);
    }

    public void setClientWithLink(ClientJpa client) {
        this.client = client;
        client.setOwner(this);
    }
}

package com.thanlinardos.resource_server.model.entity;

import com.thanlinardos.resource_server.model.entity.base.BasicAuditableJpa;
import com.thanlinardos.resource_server.model.mapped.CustomerModel;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "customer")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class CustomerJpa extends BasicAuditableJpa {

    @OneToOne(mappedBy = "customer")
    @ToString.Exclude
    private OwnerJpa owner;
    private String username;
    private String email;
    private String mobileNumber;
    private String firstName;
    private String lastName;
    @Default
    private Boolean enabled = true;
    @Default
    private Boolean accountNonExpired = true;
    @Default
    private Boolean accountNonLocked = true;
    @Default
    private Boolean credentialsNonExpired = true;

    public static CustomerJpa fromModel(CustomerModel customer) {
        CustomerJpa entity = builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .email(customer.getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .owner(OwnerJpa.builder()
                        .id(customer.getOwnerId())
                        .build())
                .mobileNumber(customer.getMobileNumber())
                .enabled(customer.getEnabled())
                .accountNonExpired(customer.getAccountNonExpired())
                .accountNonLocked(customer.getAccountNonLocked())
                .credentialsNonExpired(customer.getCredentialsNonExpired())
                .build();
        entity.setTrackedProperties(customer);
        return entity;
    }
}

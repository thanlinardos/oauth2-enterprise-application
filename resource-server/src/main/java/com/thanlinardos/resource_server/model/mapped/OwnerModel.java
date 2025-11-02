package com.thanlinardos.resource_server.model.mapped;

import com.thanlinardos.resource_server.model.entity.OwnerJpa;
import com.thanlinardos.resource_server.model.info.Client;
import com.thanlinardos.resource_server.model.info.Customer;
import com.thanlinardos.resource_server.model.info.OwnerDetailsInfo;
import com.thanlinardos.resource_server.model.info.OwnerType;
import com.thanlinardos.resource_server.model.mapped.base.BasicAuditableModel;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.PrivilegedResource;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class OwnerModel extends BasicAuditableModel implements Serializable, PrivilegedResource {

    private final UUID uuid;
    private String principalName;
    private OwnerType type;
    private int privilegeLevel;
    @Builder.Default
    private List<RoleModel> roles = new ArrayList<>();
    @Nullable
    private CustomerModel customer;
    @Nullable
    private ClientModel client;

    public OwnerModel(OwnerJpa entity) {
        super(entity);
        this.uuid = entity.getUuid();
        this.principalName = entity.getName();
        this.privilegeLevel = entity.getPrivilegeLevel();
        this.roles = entity.getRoles().stream()
                .map(RoleModel::new)
                .toList();
        if (entity.getCustomer() != null) {
            this.customer = new CustomerModel(entity.getCustomer());
        } else if (entity.getClient() != null) {
            this.client = new ClientModel(entity.getClient());
        }
        this.type = OwnerType.valueOf(entity.getType());
    }

    public OwnerDetailsInfo toInfo() {
        return switch (type) {
            case CUSTOMER -> toCustomerInfo();
            case CLIENT -> toClientInfo();
        };
    }

    public Client toClientInfo() {
        if (client == null) {
            throw new IllegalArgumentException("Client is null for owner with id " + this.getId());
        }
        return Client.builder()
                .uuid(this.uuid)
                .serviceAccountId(this.client.getServiceAccountId())
                .category(this.client.getCategory())
                .name(this.client.getName())
                .createDt(this.getCreatedAt().toLocalDate())
                .roles(this.roles)
                .build();
    }

    public Customer toCustomerInfo() {
        if (customer == null) {
            throw new IllegalArgumentException("Customer is null for owner with id " + this.getId());
        }
        return Customer.builder()
                .uuid(this.uuid)
                .name(this.customer.getUsername())
                .email(this.customer.getEmail())
                .mobileNumber(this.customer.getMobileNumber())
                .createDt(this.getCreatedAt().toLocalDate())
                .roles(this.roles)
                .build();
    }

    public Collection<String> getRoleNames() {
        return roles.stream()
                .map(RoleModel::getName)
                .toList();
    }
}

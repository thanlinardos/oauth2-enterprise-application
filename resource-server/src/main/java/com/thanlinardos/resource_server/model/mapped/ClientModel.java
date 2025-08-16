package com.thanlinardos.resource_server.model.mapped;

import com.thanlinardos.resource_server.model.entity.ClientJpa;
import com.thanlinardos.resource_server.model.mapped.base.BasicAuditableModel;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.PrivilegedResource;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class ClientModel extends BasicAuditableModel implements Serializable, PrivilegedResource {

    private UUID uuid;
    private UUID serviceAccountId;
    private String name;
    private String category;
    private int privilegeLevel;
    private long ownerId;

    public ClientModel(ClientJpa entity) {
        super(entity);
        this.name = entity.getName();
        this.category = entity.getCategory();
        this.uuid = entity.getOwner().getUuid();
        this.privilegeLevel = entity.getOwner().getPrivilegeLevel();
        this.ownerId = entity.getOwner().getId();
        this.serviceAccountId = entity.getServiceAccountId();
    }

    @Override
    public String getPrincipalName() {
        return name;
    }
}

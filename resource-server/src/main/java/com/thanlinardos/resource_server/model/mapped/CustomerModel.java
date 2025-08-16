package com.thanlinardos.resource_server.model.mapped;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.PrivilegedResource;
import com.thanlinardos.resource_server.model.entity.CustomerJpa;
import com.thanlinardos.resource_server.model.mapped.base.BasicAuditableModel;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class CustomerModel extends BasicAuditableModel implements Serializable, PrivilegedResource {

    private UUID uuid;
    private String username;
    private String email;
    private String mobileNumber;
    private String firstName;
    private String lastName;
    @Builder.Default
    private Boolean enabled = true;
    @Builder.Default
    private Boolean accountNonExpired = true;
    @Builder.Default
    private Boolean accountNonLocked = true;
    @Builder.Default
    private Boolean credentialsNonExpired = true;
    private int privilegeLevel;
    private long ownerId;

    public CustomerModel(CustomerJpa entity) {
        super(entity);
        setUuid(entity.getOwner().getUuid());
        setUsername(entity.getUsername());
        setEmail(entity.getEmail());
        setMobileNumber(entity.getMobileNumber());
        setFirstName(entity.getFirstName());
        setLastName(entity.getLastName());
        setEnabled(entity.getEnabled());
        setAccountNonExpired(entity.getAccountNonExpired());
        setAccountNonLocked(entity.getAccountNonLocked());
        setCredentialsNonExpired(entity.getCredentialsNonExpired());
        setPrivilegeLevel(entity.getOwner().getPrivilegeLevel());
        setOwnerId(entity.getOwner().getId());
    }

    @Override
    public String getPrincipalName() {
        return email;
    }
}

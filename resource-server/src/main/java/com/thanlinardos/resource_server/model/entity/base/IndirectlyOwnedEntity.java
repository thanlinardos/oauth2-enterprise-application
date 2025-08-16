package com.thanlinardos.resource_server.model.entity.base;

import com.thanlinardos.resource_server.model.entity.OwnerJpa;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.PrivilegedResource;

public interface IndirectlyOwnedEntity<T extends IndirectlyOwnedEntity<T>> extends PrivilegedResource {

    OwnerJpa getOwner();

    default int getPrivilegeLevel() {
        return getOwner().getPrivilegeLevel();
    }

    @Override
    default String getPrincipalName() {
        return getOwner().getName();
    }
}

package com.thanlinardos.resource_server.model.entity.base;

import com.thanlinardos.resource_server.model.entity.OwnerJpa;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.PrivilegedResource;

public interface OwnedEntity<T extends OwnedEntity<T>> extends PrivilegedResource {

    OwnerJpa getOwner();
    void setOwner(OwnerJpa owner);

    @SuppressWarnings("unchecked")
    default T owner(OwnerJpa owner) {
        setOwner(owner);
        return (T) this;
    }

    default int getPrivilegeLevel() {
        return getOwner().getPrivilegeLevel();
    }

    @Override
    default String getPrincipalName() {
        return getOwner().getName();
    }
}

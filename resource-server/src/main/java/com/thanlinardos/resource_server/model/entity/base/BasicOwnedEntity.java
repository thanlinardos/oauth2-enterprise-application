package com.thanlinardos.resource_server.model.entity.base;

import com.thanlinardos.resource_server.model.entity.owner.OwnerJpa;

public interface BasicOwnedEntity {

    OwnerJpa getOwner();

    void setOwner(OwnerJpa owner);
}

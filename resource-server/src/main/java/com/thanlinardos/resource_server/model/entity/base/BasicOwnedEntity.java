package com.thanlinardos.resource_server.model.entity.base;

import com.thanlinardos.resource_server.model.entity.OwnerJpa;

public interface BasicOwnedEntity {

    OwnerJpa getOwner();

    void setOwner(OwnerJpa owner);
}

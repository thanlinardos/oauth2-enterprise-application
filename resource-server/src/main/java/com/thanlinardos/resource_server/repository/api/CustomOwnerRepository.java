package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.owner.OwnerJpa;

public interface CustomOwnerRepository {

    /**
     * Deletes the owner and the associated entities (customer, client, account) in a cascading manner.
     *
     * @param owner the owner entity to be deleted, along with its associated entities.
     */
    void deleteCascade(OwnerJpa owner);
}

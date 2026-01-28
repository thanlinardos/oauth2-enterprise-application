package com.thanlinardos.resource_server.repository;

import com.thanlinardos.resource_server.model.entity.owner.OwnerJpa;
import com.thanlinardos.resource_server.repository.api.CustomOwnerRepository;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** IMPORTANT: In order for jpa to pickup custom implementations, they need to follow this exact format as shown in this class: <interface name extending Jpa- or CrudRepository>Impl */
@RequiredArgsConstructor
@Repository
public class OwnerRepositoryImpl implements CustomOwnerRepository {

    private final EntityManager entityManager;

    @Override
    public void deleteCascade(OwnerJpa owner) {
        entityManager.remove(owner);
        removeIfNotNull(owner.getCustomer());
        removeIfNotNull(owner.getClient());
        removeIfNotNull(owner.getAccount());
    }

    private void removeIfNotNull(BasicIdJpa entity) {
        Optional.ofNullable(entity)
                .ifPresent(entityManager::remove);
    }
}

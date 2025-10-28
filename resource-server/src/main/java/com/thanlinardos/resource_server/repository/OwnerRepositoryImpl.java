package com.thanlinardos.resource_server.repository;

import com.thanlinardos.resource_server.model.entity.OwnerJpa;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import com.thanlinardos.resource_server.repository.api.CustomOwnerRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

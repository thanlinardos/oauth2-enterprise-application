package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.model.entity.ClientJpa;
import com.thanlinardos.resource_server.model.info.Client;
import com.thanlinardos.resource_server.model.mapped.ClientModel;
import com.thanlinardos.resource_server.model.mapped.OwnerModel;
import com.thanlinardos.resource_server.service.userservice.api.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final EntityManager entityManager;
    private final UserService userService;

    public List<ClientModel> getClients() {
        return Optional.of(entityManager.createQuery("from ClientJpa", ClientJpa.class)
                .getResultList().stream()
                .map(ClientModel::new)
                .toList())
                .orElseGet(userService::getAllClients);
    }

    public Optional<ClientModel> getClientByName(String name) {
        return getClientJpaByName(name)
                .map(ClientModel::new)
                .or(() -> userService.getClientByNameAndPersist(name));
    }

    public Optional<OwnerModel> getOwnerByServiceAccountId(UUID serviceAccountId) {
        return entityManager.createQuery("from ClientJpa c left join fetch c.owner where c.serviceAccountId=:serviceAccountId", ClientJpa.class)
                .setParameter("serviceAccountId", serviceAccountId)
                .getResultList().stream()
                .map(ClientJpa::getOwner)
                .map(OwnerModel::new)
                .findFirst();
    }

    public Optional<Client> getClientInfoByName(String name) {
        return getClientJpaByName(name)
                .map(ClientJpa::getOwner)
                .map(OwnerModel::new)
                .map(OwnerModel::toClientInfo)
                .or(() -> userService.getClientInfoByName(name));
    }

    private Optional<ClientJpa> getClientJpaByName(String name) {
        return entityManager.createQuery("from ClientJpa where name=:name", ClientJpa.class)
                .setParameter("name", name)
                .getResultList().stream()
                .findFirst();
    }
}

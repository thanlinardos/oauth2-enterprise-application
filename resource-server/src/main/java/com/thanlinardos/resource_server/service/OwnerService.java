package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.misc.utils.EntityUtils;
import com.thanlinardos.resource_server.model.constants.SecurityConstants;
import com.thanlinardos.resource_server.model.entity.OwnerJpa;
import com.thanlinardos.resource_server.model.entity.RoleJpa;
import com.thanlinardos.resource_server.misc.utils.ParserUtil;
import com.thanlinardos.resource_server.model.entity.base.BasicIdJpa;
import com.thanlinardos.resource_server.model.mapped.OwnerModel;
import com.thanlinardos.resource_server.service.roleservice.RoleServiceImpl;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final EntityManager entityManager;
    private final RoleServiceImpl roleService;

    @Transactional
    public OwnerModel save(OwnerModel owner) {
        OwnerJpa entity = OwnerJpa.fromModel(owner);
        List<RoleJpa> roles = owner.getRoles().stream()
                .map(RoleJpa::fromModel)
                .toList();
        entity.setRoles(roles);
        entity = EntityUtils.saveOrUpdate(entity, entityManager);
        owner.setId(entity.getId());
        return owner;
    }

    @Transactional
    public OwnerModel saveGuest(OwnerModel owner) {
        OwnerJpa entity = OwnerJpa.fromModel(owner);
        List<RoleJpa> defaultGuestRoles = findDefaultGuestRoles();
        entity.setRoles(defaultGuestRoles);
        entity.setPrivilegeLevel(getDefaultGuestPrivilegeLvl(defaultGuestRoles));
        entity = EntityUtils.saveOrUpdate(entity, entityManager);
        owner.setId(entity.getId());
        return owner;
    }

    private @NotNull Integer getDefaultGuestPrivilegeLvl(List<RoleJpa> defaultGuestRoles) {
        return defaultGuestRoles.stream()
                .map(RoleJpa::getPrivilegeLvl)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
    }

    @Transactional
    public Optional<Integer> delete(UUID uuid) {
        return getOwnerFromUUID(uuid)
                .map(this::deleteOwnerCascadeClientOrCustomer);
    }

    private int deleteOwnerCascadeClientOrCustomer(OwnerJpa owner) {
        int deleted = 0;
        deleted += removeEntityOrZero(owner.getCustomer());
        deleted += removeEntityOrZero(owner.getClient());
        deleted += removeEntityOrZero(owner.getAccount());
        deleted += removeEntity(owner);
        return deleted;
    }

    private int removeEntityOrZero(@Nullable BasicIdJpa entity) {
        return Optional.ofNullable(entity)
                .map(this::removeEntity)
                .orElse(0);
    }

    private int removeEntity(BasicIdJpa e) {
        entityManager.remove(e);
        return 1;
    }

    private Optional<OwnerJpa> getOwnerFromUUID(UUID uuid) {
        return entityManager.createQuery("from OwnerJpa where uuid=:uuid", OwnerJpa.class)
                .setParameter("uuid", uuid)
                .getResultList().stream()
                .findFirst();
    }

    private List<RoleJpa> findDefaultGuestRoles() {
        return roleService.findRoles(SecurityConstants.DEFAULT_GUEST_ROLES).stream()
                .map(RoleJpa::fromModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<OwnerModel> getOwnerByPrincipalName(String name) {
        return ParserUtil.safeParseOptionalUUID(name)
                .flatMap(this::getOwnerByServiceAccountId)
                .or(() -> getOwnerByName(name));
    }

    public Optional<OwnerModel> getOwnerByName(String name) {
        return entityManager.createQuery("from OwnerJpa where name=:name", OwnerJpa.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .map(OwnerModel::new);
    }

    public Optional<OwnerModel> getOwnerByUuid(UUID uuid) {
        return getOwnerFromUUID(uuid)
                .map(OwnerModel::new);
    }

    public Optional<OwnerModel> getOwnerByServiceAccountId(UUID uuid) {
        return entityManager.createQuery("from OwnerJpa o join fetch o.client where o.client.serviceAccountId=:uuid", OwnerJpa.class)
                .setParameter("uuid", uuid)
                .getResultStream()
                .findFirst()
                .map(OwnerModel::new);
    }
}

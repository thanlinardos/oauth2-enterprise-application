package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.model.constants.SecurityConstants;
import com.thanlinardos.resource_server.model.entity.OwnerJpa;
import com.thanlinardos.resource_server.model.entity.RoleJpa;
import com.thanlinardos.resource_server.model.mapped.OwnerModel;
import com.thanlinardos.resource_server.repository.api.OwnerRepository;
import com.thanlinardos.resource_server.service.roleservice.RoleServiceImpl;
import com.thanlinardos.spring_enterprise_library.parse.utils.ParserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final RoleServiceImpl roleService;

    @Transactional
    public OwnerModel save(OwnerModel owner) {
        OwnerJpa entity = OwnerJpa.fromModel(owner);
        List<RoleJpa> roles = owner.getRoles().stream()
                .map(RoleJpa::fromModel)
                .toList();
        entity.setRoles(roles);
        entity = ownerRepository.save(entity);
        owner.setId(entity.getId());
        return owner;
    }

    @Transactional
    public OwnerModel saveGuest(OwnerModel owner) {
        OwnerJpa entity = OwnerJpa.fromModel(owner);
        List<RoleJpa> defaultGuestRoles = findDefaultGuestRoles();
        entity.setRoles(defaultGuestRoles);
        entity.setPrivilegeLevel(getDefaultGuestPrivilegeLvl(defaultGuestRoles));
        entity = ownerRepository.save(entity);
        owner.setId(entity.getId());
        return owner;
    }

    private @Nonnull Integer getDefaultGuestPrivilegeLvl(List<RoleJpa> defaultGuestRoles) {
        return defaultGuestRoles.stream()
                .map(RoleJpa::getPrivilegeLvl)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
    }

    @Transactional
    public Optional<Integer> delete(UUID uuid) {
        return ownerRepository.getFirstByUuid(uuid)
                .map(this::deleteCascade);
    }

    private int deleteCascade(OwnerJpa ownerJpa) {
        ownerRepository.deleteCascade(ownerJpa);
        return 1;
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

    private Optional<OwnerModel> getOwnerByServiceAccountId(UUID uuid) {
        return ownerRepository.getFirstByClient_ServiceAccountId(uuid)
                .map(OwnerModel::new);
    }

    public Optional<OwnerModel> getOwnerByName(String name) {
        return ownerRepository.getFirstByName(name)
                .map(OwnerModel::new);
    }

    public Optional<OwnerModel> getOwnerByUuid(UUID uuid) {
        return ownerRepository.getFirstByUuid(uuid)
                .map(OwnerModel::new);
    }

    public boolean ownerExistsByPrincipalName(String name) {
        return ParserUtil.safeParseOptionalUUID(name)
                .map(ownerRepository::existsByClient_ServiceAccountId)
                .orElse(false);
    }
}

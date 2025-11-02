package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.misc.utils.RoleUtils;
import com.thanlinardos.resource_server.model.info.Client;
import com.thanlinardos.resource_server.model.info.Customer;
import com.thanlinardos.resource_server.model.info.OwnerType;
import com.thanlinardos.resource_server.model.mapped.OwnerModel;
import com.thanlinardos.resource_server.model.mapped.RoleModel;
import com.thanlinardos.resource_server.security.keycloak.KeycloakServiceUtils;
import com.thanlinardos.resource_server.service.roleservice.api.OauthRoleService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.PrivilegedResource;
import com.thanlinardos.spring_enterprise_library.time.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.AbstractUserRepresentation;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.thanlinardos.spring_enterprise_library.time.utils.DateUtils.getLocalDateFromEpochMilli;

@Service
@Slf4j
public class KeycloakMappingService {

    private static final String KEYCLOAK = "KEYCLOAK";

    private final OauthRoleService roleService;

    public KeycloakMappingService(OauthRoleService roleService) {
        this.roleService = roleService;
    }

    public OwnerModel mapUserResourceToOwnerModel(UserResource userResource) {
        UserRepresentation user = userResource.toRepresentation();
        List<RoleModel> userRoles = getUserRoles(userResource);
        int privilegeLevel = PrivilegedResource.calcPrivilegeLvlFromRoles(userRoles);
        LocalDateTime createdAt = DateUtils.getLocalDateTimeFromEpochMilli(user.getCreatedTimestamp());
        return OwnerModel.builder()
                .uuid(UUID.fromString(user.getId()))
                .principalName(user.getEmail())
                .roles(userRoles)
                .privilegeLevel(privilegeLevel)
                .customer(KeycloakServiceUtils.mapUserResourceToCustomerModel(user, privilegeLevel))
                .type(OwnerType.CUSTOMER)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .createdBy(KEYCLOAK)
                .updatedBy(KEYCLOAK)
                .build();
    }

    public List<RoleModel> getUserRoles(UserResource user) {
        return parseRoles(user.roles().realmLevel().listAll());
    }

    public Customer mapUserResourceToCustomer(UserResource userResource) {
        UserRepresentation user = userResource.toRepresentation();
        LocalDate createdAt = getLocalDateFromEpochMilli(user.getCreatedTimestamp());
        return Customer.builder()
                .name(user.getUsername())
                .email(user.getEmail())
                .createDt(createdAt)
                .roles(getUserRoles(userResource))
                .build();
    }

    public Client mapClientResourceToClient(ClientResource clientResource) {
        ClientRepresentation client = clientResource.toRepresentation();
        LocalDate createdAt = getLocalDateFromEpochMilli(clientResource.getServiceAccountUser().getCreatedTimestamp());
        return Client.builder()
                .name(client.getClientId())
                .createDt(createdAt)
                .roles(parseRoles(clientResource.roles().list()))
                .serviceAccountId(UUID.fromString(clientResource.getServiceAccountUser().getId()))
                .build();
    }

    public OwnerModel mapClientResourceToOwnerModel(ClientResource clientResource, List<RoleModel> serviceAccountRoles) {
        ClientRepresentation client = clientResource.toRepresentation();
        List<RoleModel> clientRoles = new ArrayList<>(parseRoles(clientResource.roles().list()));
        clientRoles.addAll(serviceAccountRoles);
        int privilegeLevel = PrivilegedResource.calcPrivilegeLvlFromRoles(clientRoles);
        Optional<UserRepresentation> serviceAccountUser = KeycloakServiceUtils.getServiceAccountUser(clientResource);
        LocalDateTime createdAt = DateUtils.getLocalDateTimeFromEpochMilli(serviceAccountUser
                .map(UserRepresentation::getCreatedTimestamp)
                .orElse(System.currentTimeMillis()));
        String serviceAccountId = serviceAccountUser
                .map(AbstractUserRepresentation::getId)
                .orElse(null);
        return OwnerModel.builder()
                .uuid(UUID.fromString(client.getId()))
                .principalName(client.getClientId())
                .roles(clientRoles)
                .privilegeLevel(privilegeLevel)
                .client(KeycloakServiceUtils.mapClientResourceToClientModel(client, privilegeLevel, createdAt, serviceAccountId))
                .type(OwnerType.CLIENT)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .createdBy(KEYCLOAK)
                .updatedBy(KEYCLOAK)
                .build();
    }

    private List<RoleModel> parseRoles(List<RoleRepresentation> roles) {
        Collection<String> roleNames = RoleUtils.getRoleNamesFromRoleRepresentations(roles);
        return (List<RoleModel>) roleService.findRoles(roleNames);
    }
}

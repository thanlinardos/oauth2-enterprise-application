package com.thanlinardos.resource_server.service.userservice;

import com.thanlinardos.resource_server.model.constants.SecurityConstants;
import com.thanlinardos.resource_server.model.info.*;
import com.thanlinardos.resource_server.model.mapped.ClientModel;
import com.thanlinardos.resource_server.model.mapped.CustomerModel;
import com.thanlinardos.resource_server.model.mapped.OwnerModel;
import com.thanlinardos.resource_server.model.mapped.RoleModel;
import com.thanlinardos.resource_server.security.keycloak.KeycloakServiceUtils;
import com.thanlinardos.resource_server.service.KeycloakMappingService;
import com.thanlinardos.resource_server.service.OwnerService;
import com.thanlinardos.resource_server.service.userservice.api.UserService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types.OperationType;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils.ModelUtils;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.thanlinardos.resource_server.security.keycloak.KeycloakServiceUtils.handleRequest;
import static com.thanlinardos.spring_enterprise_library.parse.utils.ParserUtil.getPathParameterFromLocationURI;

@Slf4j
public class KeycloakUserService implements UserService {

    private final OwnerService ownerService;
    private final RealmResource realm;
    private final KeycloakMappingService keycloakMappingService;

    public KeycloakUserService(OwnerService ownerService, Keycloak keycloak, @Value("${oauth2.keycloak.realm}") String realmName, KeycloakMappingService keycloakMappingService) {
        this.ownerService = ownerService;
        this.keycloakMappingService = keycloakMappingService;
        log.info("KeycloakUserService created: realmName={}", realmName);
        realm = keycloak.realm(realmName);
    }

    @Override
    public List<CustomerModel> getAllCustomers() {
        return realm.users().list().stream()
                .map(AbstractUserRepresentation::getId)
                .map(realm.users()::get)
                .map(this::mapAndPersistUserResourceToOwnerModel)
                .map(OwnerModel::getCustomer)
                .toList();
    }

    @Override
    public List<ClientModel> getAllClients() {
        return realm.clients().findAll().stream()
                .map(ClientRepresentation::getId)
                .map(this::getClientByUuid)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Transactional
    @Override
    public String createCustomer(RegisterCustomerDetails customerDetails) {
        UserRepresentation user = mapCustomerDetailsToUserRepresentation(customerDetails);
        String userId = tryCreateUser(user);
        tryAddRealmRolesToUser(userId, SecurityConstants.DEFAULT_GUEST_ROLES_NO_PREFIX);
        tryUpdatePasswordCredentialToUser(userId, customerDetails.getPassword());
        return getGuestOwnerByEmailAndPersist(customerDetails.getEmail())
                .orElseThrow(() -> new InternalServerErrorException("Failed to find created guest user"));
    }

    private UserRepresentation mapCustomerDetailsToUserRepresentation(RegisterCustomerDetails customerDetails) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(customerDetails.getEmail());
        user.setEmail(customerDetails.getEmail());
        user.setEnabled(true);
        user.setFirstName(customerDetails.getFirstName());
        user.setLastName(customerDetails.getLastName());
        user.setAttributes(Map.of("mobileNumber", Collections.singletonList(customerDetails.getMobileNumber())));
        return user;
    }

    @Transactional
    @Override
    public OwnerModel createCustomerWithRoles(Customer customer) {
        UserRepresentation user = mapCustomerDetailsToUserRepresentation(customer.toRegisterCustomerDetails());
        String userId = tryCreateUser(user);
        Set<String> realmRoles = customer.getRoles().stream()
                .map(RoleModel::getName)
                .collect(Collectors.toSet());
        tryAddRealmRolesToUser(userId, realmRoles);
        tryUpdatePasswordCredentialToUser(userId, customer.getPassword());
        return getOwnerByEmailAndPersist(customer.getEmail())
                .orElseThrow(() -> new InternalServerErrorException("Failed to create user: " + customer));
    }

    private String tryCreateUser(UserRepresentation user) {
        Response response = KeycloakServiceUtils.handleRequest(realm.users()::create, user, OperationType.CREATE);
        return getPathParameterFromLocationURI(response);
    }

    private void tryAddRealmRolesToUser(String userId, Set<String> roles) {
        List<RoleRepresentation> roleRepresentations = roles.stream()
                .map(role -> realm.roles().get(role).toRepresentation())
                .toList();
        UserResource user = realm.users().get(userId);
        handleRequest(user.roles().realmLevel()::add, roleRepresentations, OperationType.ADD);
    }

    private void tryUpdatePasswordCredentialToUser(String userId, String password) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        UserResource user = realm.users().get(userId);
        handleRequest(user::resetPassword, passwordCred, OperationType.UPDATE);
    }

    @Override
    public Optional<CustomerModel> getCustomerByUsername(String username) {
        return getUserResourceByUsername(username)
                .map(this::mapAndPersistUserResourceToOwnerModel)
                .map(OwnerModel::getCustomer);
    }

    @Override
    public Optional<CustomerModel> getCustomerByEmailAndPersist(String email) {
        return getOwnerByEmailAndPersist(email)
                .map(OwnerModel::getCustomer);
    }

    private Optional<OwnerModel> getOwnerByEmailAndPersist(String email) {
        return getUserResourceByEmail(email)
                .map(this::mapAndPersistUserResourceToOwnerModel);
    }

    @Override
    public Optional<OwnerModel> getOwnerByIdAndPersistOrUpdate(UUID resourceUuid, OwnerType ownerType) {
        OwnerModel owner = ownerService.getOwnerByUuid(resourceUuid)
                .orElse(null);
        Long ownerId = ModelUtils.getIdFromModel(owner);
        Long resourceId = ModelUtils.getIdFromNestedModelOr(owner, OwnerModel::getCustomer, OwnerModel::getClient);
        return switch (ownerType) {
            case CUSTOMER -> Optional.ofNullable(realm.users().get(resourceUuid.toString()))
                    .map(userResource -> mapAndPersistOrUpdateUserResourceToOwnerModel(userResource, ownerId, resourceId));
            case CLIENT -> {
                ClientResource client = realm.clients().get(resourceUuid.toString());
                yield Optional.ofNullable(client)
                        .map(clientResource -> mapAndPersistOrUpdateClientResourceToOwnerModel(clientResource, ownerId, resourceId, getServiceAccountRoles(clientResource)));
            }
        };
    }

    private List<RoleModel> getServiceAccountRoles(ClientResource clientResource) {
        try {
            String serviceAccountId = clientResource.getServiceAccountUser().getId();
            return keycloakMappingService.getUserRoles(realm.users().get(serviceAccountId));
        } catch (BadRequestException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<String> getGuestOwnerByEmailAndPersist(String email) {
        return getUserResourceByEmail(email)
                .map(this::mapAndPersistGuestUserResourceToOwnerModel);
    }

    public Optional<CustomerModel> getCustomerByUsernameOrEmailAndPersist(String name) {
        return getUserResourceByEmail(name)
                .or(() -> getUserResourceByUsername(name))
                .map(this::mapAndPersistUserResourceToOwnerModel)
                .map(OwnerModel::getCustomer);
    }

    public Optional<ClientModel> getClientByServiceAccountUuid(String uuid) {
        return realm.clients().findAll().stream()
                .map(ClientRepresentation::getId)
                .map(realm.clients()::get)
                .filter(clientResource -> clientResource.getServiceAccountUser().getId().equals(uuid))
                .findFirst()
                .map(clientResource -> mapAndPersistClientResourceToOwnerModel(clientResource, keycloakMappingService.getUserRoles(realm.users().get(uuid))))
                .map(OwnerModel::getClient);
    }

    @Override
    public Optional<ClientModel> getClientByUuid(String uuid) {
        return Optional.ofNullable(realm.clients())
                .map(clientsResource -> clientsResource.get(uuid))
                .map(clientResource -> mapAndPersistClientResourceToOwnerModel(clientResource, getServiceAccountRoles(clientResource)))
                .map(OwnerModel::getClient);
    }

    @Override
    public Optional<Customer> getCustomerInfoByEmail(String email) {
        return getUserResourceByEmail(email)
                .map(keycloakMappingService::mapUserResourceToCustomer);
    }


    private String mapAndPersistGuestUserResourceToOwnerModel(UserResource userResource) {
        OwnerModel owner = keycloakMappingService.mapUserResourceToOwnerModel(userResource);
        ownerService.saveGuest(owner);
        return owner.getPrincipalName();
    }

    private OwnerModel mapAndPersistUserResourceToOwnerModel(UserResource userResource) {
        OwnerModel owner = keycloakMappingService.mapUserResourceToOwnerModel(userResource);
        return ownerService.save(owner);
    }

    private OwnerModel mapAndPersistOrUpdateUserResourceToOwnerModel(UserResource userResource, @Nullable Long ownerId, @Nullable Long customerId) {
        OwnerModel owner = keycloakMappingService.mapUserResourceToOwnerModel(userResource);
        owner.setId(ownerId);
        Optional.ofNullable(owner.getCustomer())
                .ifPresentOrElse(
                        customer -> customer.setId(customerId),
                        () -> {
                            throw new InternalServerErrorException("Error mapping user resource to owner with name: " + owner.getPrincipalName());
                        }
                );
        return owner.getRoles().isEmpty() ? ownerService.saveGuest(owner) : ownerService.save(owner);
    }

    private OwnerModel mapAndPersistOrUpdateClientResourceToOwnerModel(ClientResource clientResource, @Nullable Long ownerId, @Nullable Long clientId, List<RoleModel> serviceAccountRoles) {
        OwnerModel owner = keycloakMappingService.mapClientResourceToOwnerModel(clientResource, serviceAccountRoles);
        owner.setId(ownerId);
        Optional.ofNullable(owner.getClient())
                .ifPresentOrElse(
                        client -> client.setId(clientId),
                        () -> {
                            throw new InternalServerErrorException("Error mapping client resource to owner with name: " + owner.getPrincipalName());
                        }
                );
        return owner.getRoles().isEmpty() ? ownerService.saveGuest(owner) : ownerService.save(owner);
    }

    private OwnerModel mapAndPersistClientResourceToOwnerModel(ClientResource clientResource, List<RoleModel> serviceAccountRoles) {
        OwnerModel owner = keycloakMappingService.mapClientResourceToOwnerModel(clientResource, serviceAccountRoles);
        return ownerService.save(owner);
    }

    private Optional<UserResource> getUserResourceByEmail(String email) {
        return realm.users().searchByEmail(email, true).stream()
                .map(UserRepresentation::getId)
                .findFirst()
                .map(realm.users()::get);
    }

    private Optional<UserResource> getUserResourceByUsername(String username) {
        return realm.users().searchByUsername(username, true).stream()
                .map(UserRepresentation::getId)
                .findFirst()
                .map(realm.users()::get);
    }

    @Override
    public Optional<ClientModel> getClientByNameAndPersist(String name) {
        return findClientResourceByName(name)
                .map(clientResource -> mapAndPersistClientResourceToOwnerModel(clientResource, getServiceAccountRoles(clientResource)))
                .map(OwnerModel::getClient);
    }

    @Override
    public Optional<Client> getClientInfoByName(String name) {
        return findClientResourceByName(name)
                .map(keycloakMappingService::mapClientResourceToClient);
    }

    private Optional<ClientResource> findClientResourceByName(String name) {
        return realm.clients().findAll().stream()
                .filter(client -> client.getClientId().equals(name))
                .findFirst()
                .map(ClientRepresentation::getId)
                .map(realm.clients()::get);
    }
}

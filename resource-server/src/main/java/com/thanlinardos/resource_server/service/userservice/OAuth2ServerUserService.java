package com.thanlinardos.resource_server.service.userservice;

import com.thanlinardos.resource_server.model.info.Client;
import com.thanlinardos.resource_server.model.info.Customer;
import com.thanlinardos.resource_server.model.info.OwnerType;
import com.thanlinardos.resource_server.model.info.RegisterCustomerDetails;
import com.thanlinardos.resource_server.model.mapped.ClientModel;
import com.thanlinardos.resource_server.model.mapped.CustomerModel;
import com.thanlinardos.resource_server.model.mapped.OwnerModel;
import com.thanlinardos.resource_server.service.OwnerService;
import com.thanlinardos.resource_server.service.userservice.api.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class OAuth2ServerUserService implements UserService {

    private final OwnerService ownerService;

    public OAuth2ServerUserService(OwnerService ownerService, String authServerName) {
        this.ownerService = ownerService;
        log.info("OAuth2ServerUserService created for authorization server: {}", authServerName);
    }

    @Override
    public List<CustomerModel> getAllCustomers() {
        return List.of();
    }

    @Override
    public List<ClientModel> getAllClients() {
        return List.of();
    }

    @Override
    public String createCustomer(RegisterCustomerDetails customerDetails) {
        return "";
    }

    @Override
    public OwnerModel createCustomerWithRoles(Customer customer) {
        return null;
    }

    @Override
    public Optional<CustomerModel> getCustomerByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<CustomerModel> getCustomerByEmailAndPersist(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<OwnerModel> getOwnerByIdAndPersistOrUpdate(UUID resourceUuid, OwnerType ownerType) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getGuestOwnerByEmailAndPersist(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<ClientModel> getClientByUuid(String uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<Customer> getCustomerInfoByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<ClientModel> getClientByNameAndPersist(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Client> getClientInfoByName(String name) {
        return Optional.empty();
    }
}

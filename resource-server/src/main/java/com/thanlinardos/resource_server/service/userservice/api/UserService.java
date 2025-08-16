package com.thanlinardos.resource_server.service.userservice.api;

import com.thanlinardos.resource_server.model.info.Client;
import com.thanlinardos.resource_server.model.info.Customer;
import com.thanlinardos.resource_server.model.info.OwnerType;
import com.thanlinardos.resource_server.model.info.RegisterCustomerDetails;
import com.thanlinardos.resource_server.model.mapped.ClientModel;
import com.thanlinardos.resource_server.model.mapped.CustomerModel;
import com.thanlinardos.resource_server.model.mapped.OwnerModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    List<CustomerModel> getAllCustomers();

    List<ClientModel> getAllClients();

    String createCustomer(RegisterCustomerDetails customerDetails);

    @Transactional
    OwnerModel createCustomerWithRoles(Customer customer);

    Optional<CustomerModel> getCustomerByUsername(String username);

    Optional<CustomerModel> getCustomerByEmailAndPersist(String email);

    Optional<OwnerModel> getOwnerByIdAndPersistOrUpdate(UUID resourceUuid, OwnerType ownerType);

    Optional<String> getGuestOwnerByEmailAndPersist(String email);

    Optional<ClientModel> getClientByUuid(String uuid);

    Optional<Customer> getCustomerInfoByEmail(String email);

    Optional<ClientModel> getClientByNameAndPersist(String name);

    Optional<Client> getClientInfoByName(String name);
}

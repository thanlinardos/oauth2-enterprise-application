package com.thanlinardos.resource_server.service.owner;

import com.thanlinardos.resource_server.model.entity.owner.CustomerJpa;
import com.thanlinardos.resource_server.model.info.Customer;
import com.thanlinardos.resource_server.model.mapped.CustomerModel;
import com.thanlinardos.resource_server.model.mapped.OwnerModel;
import com.thanlinardos.resource_server.repository.api.CustomerRepository;
import com.thanlinardos.resource_server.service.user.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserService userService;
    private final CustomerRepository customerRepository;

    public List<CustomerModel> getCustomers() {
        return Optional.of(customerRepository.findAll().stream()
                .map(CustomerModel::new)
                .toList())
                .orElseGet(() -> userService.syncAndGetAllCustomers(false));
    }

    public CustomerModel getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(CustomerModel::new)
                .orElseGet(() -> userService.getCustomerByEmailAndPersist(email));
    }

    public Customer getCustomerInfoByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(CustomerJpa::getOwner)
                .map(OwnerModel::new)
                .map(OwnerModel::toCustomerInfo)
                .orElseGet(() -> userService.getCustomerInfoByEmail(email));
    }
}

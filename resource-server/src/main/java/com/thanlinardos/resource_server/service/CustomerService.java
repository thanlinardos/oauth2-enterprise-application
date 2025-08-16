package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.model.entity.CustomerJpa;
import com.thanlinardos.resource_server.model.info.Customer;
import com.thanlinardos.resource_server.model.mapped.CustomerModel;
import com.thanlinardos.resource_server.model.mapped.OwnerModel;
import com.thanlinardos.resource_server.service.userservice.api.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final EntityManager entityManager;
    private final UserService userService;

    public List<CustomerModel> getCustomers() {
        return Optional.of(entityManager.createQuery("from CustomerJpa", CustomerJpa.class)
                .getResultList().stream()
                .map(CustomerModel::new)
                .toList())
                .orElseGet(userService::getAllCustomers);
    }

    public Optional<CustomerModel> getCustomerByEmail(String email) {
        return getCustomerJpaByEmail(email)
                .map(CustomerModel::new)
                .or(() -> userService.getCustomerByEmailAndPersist(email));
    }

    public Optional<Customer> getCustomerInfoByEmail(String email) {
        return getCustomerJpaByEmail(email)
                .map(CustomerJpa::getOwner)
                .map(OwnerModel::new)
                .map(OwnerModel::toCustomerInfo)
                .or(() -> userService.getCustomerInfoByEmail(email));
    }

    private Optional<CustomerJpa> getCustomerJpaByEmail(String email) {
        return entityManager.createQuery("from CustomerJpa where email=:email", CustomerJpa.class)
                .setParameter("email", email)
                .getResultList().stream()
                .findFirst();
    }
}

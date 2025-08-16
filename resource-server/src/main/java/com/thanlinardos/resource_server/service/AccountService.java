package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.model.entity.AccountJpa;
import com.thanlinardos.resource_server.model.mapped.AccountModel;
import com.thanlinardos.resource_server.service.userservice.api.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final EntityManager entityManager;
    private final UserService userService;

    public AccountModel saveAccount(AccountModel account) {
        AccountJpa entity = AccountJpa.fromModel(account);
        entityManager.persist(entity);
        account.setId(entity.getId());
        return account;
    }

    public AccountModel getAccountByAccountNumber(String accountNumber) {
        return getAccountOptionalByAccountNumber(accountNumber)
                .map(AccountModel::new)
                .orElse(null);
    }

    private Optional<AccountJpa> getAccountOptionalByAccountNumber(String accountNumber) {
        return entityManager
                .createQuery("from AccountJpa a where a.accountNumber = :accountNumber", AccountJpa.class)
                .setParameter("accountNumber", accountNumber)
                .getResultList().stream()
                .findFirst();
    }

    private Optional<AccountJpa> getAccountJpaByCustomerEmail(String username) {
        return entityManager
                .createQuery("from AccountJpa a where a.owner.customer.username = :username", AccountJpa.class)
                .setParameter("username", username)
                .getResultList().stream()
                .findFirst();
    }

    public Optional<AccountModel> getAccountByCustomerEmail(String username) {
        return getAccountJpaByCustomerEmail(username)
                .map(AccountModel::new);
    }
}

package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.model.entity.AccountTransactionJpa;
import com.thanlinardos.resource_server.model.mapped.AccountTransactionModel;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountTransactionsService {

    private final EntityManager entityManager;

    @Transactional
    public List<AccountTransactionModel> getAccountTransactionsByPrincipalNameByTransactionDtDesc(String name) {
        return entityManager
                .createQuery("""
                        from AccountTransactionJpa at
                        join fetch at.account acc
                        join fetch acc.owner owner
                        join fetch owner.roles roles
                        where owner.name = :name
                        order by at.transactionDt desc
                        """, AccountTransactionJpa.class)
                .setParameter("name", name)
                .getResultStream()
                .map(AccountTransactionModel::new)
                .toList();
    }
}

package com.thanlinardos.resource_server.repository;

import com.thanlinardos.resource_server.model.entity.account.AccountTransactionJpa;
import com.thanlinardos.resource_server.repository.api.CustomAccountTransactionRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/** IMPORTANT: In order for jpa to pickup custom implementations, they need to follow this exact format as shown in this class: <interface name extending Jpa- or CrudRepository>Impl */
@RequiredArgsConstructor
@Repository
public class AccountTransactionRepositoryImpl implements CustomAccountTransactionRepository {

    private final EntityManager entityManager;

    @Override
    public List<AccountTransactionJpa> getAccountTransactionsByPrincipalNameByTransactionDtDesc(String name) {
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
                .getResultList();
    }
}

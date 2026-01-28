package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.account.AccountTransactionJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionRepository extends JpaRepository<AccountTransactionJpa, Long>, CustomAccountTransactionRepository {
}

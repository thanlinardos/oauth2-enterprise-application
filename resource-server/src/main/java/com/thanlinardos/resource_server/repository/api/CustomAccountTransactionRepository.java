package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.account.AccountTransactionJpa;

import java.util.List;

public interface CustomAccountTransactionRepository {

    List<AccountTransactionJpa> getAccountTransactionsByPrincipalNameByTransactionDtDesc(String name);
}

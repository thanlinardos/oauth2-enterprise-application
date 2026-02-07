package com.thanlinardos.resource_server.service.account;

import com.thanlinardos.resource_server.model.mapped.AccountTransactionModel;
import com.thanlinardos.resource_server.repository.api.AccountTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountTransactionsService {

    private final AccountTransactionRepository repository;

    @Transactional
    public List<AccountTransactionModel> getAccountTransactionsByPrincipalNameByTransactionDtDesc(String name) {
        return repository.getAccountTransactionsByPrincipalNameByTransactionDtDesc(name).stream()
                .map(AccountTransactionModel::new)
                .toList();
    }
}

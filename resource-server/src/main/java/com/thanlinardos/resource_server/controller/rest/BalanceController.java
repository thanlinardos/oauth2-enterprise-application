package com.thanlinardos.resource_server.controller.rest;

import com.thanlinardos.resource_server.model.mapped.AccountTransactionModel;
import com.thanlinardos.resource_server.service.account.AccountTransactionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BalanceController {

    private final AccountTransactionsService accountTransactionsService;

    @GetMapping("/myBalance")
    public ResponseEntity<List<AccountTransactionModel>> getBalanceDetails(@RequestParam String email) {
        return ResponseEntity.ok(accountTransactionsService.getAccountTransactionsByPrincipalNameByTransactionDtDesc(email));
    }
}

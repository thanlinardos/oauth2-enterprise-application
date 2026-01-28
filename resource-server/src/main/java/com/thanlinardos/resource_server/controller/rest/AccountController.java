package com.thanlinardos.resource_server.controller.rest;

import com.thanlinardos.resource_server.model.info.RegisterAccountDetails;
import com.thanlinardos.resource_server.model.mapped.AccountModel;
import com.thanlinardos.resource_server.service.account.AccountService;
import com.thanlinardos.spring_enterprise_library.https.utils.RestControllerUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/myAccount")
    public ResponseEntity<AccountModel> getAccountDetails(@RequestParam String email) {
        AccountModel account = accountService.getAccountByCustomerEmail(email).orElse(null);
        return RestControllerUtils.getOkResponseWithBodyOrNotFound(account);
    }

    @PostMapping("/createAccount")
    public ResponseEntity<AccountModel> createAccount(@Valid @RequestBody RegisterAccountDetails registerAccountDetails) {
        AccountModel newAccount = accountService.saveAccount(registerAccountDetails);
        return ResponseEntity.ok(Objects.requireNonNull(newAccount));
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<AccountModel> getAccountByAccountNumber(@PathVariable long accountNumber) {
        AccountModel account = accountService.getAccountByAccountNumber(accountNumber).orElse(null);
        return RestControllerUtils.getOkResponseWithBodyOrNotFound(account);
    }
}

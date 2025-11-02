package com.thanlinardos.resource_server.controller.rest;

import com.thanlinardos.resource_server.model.mapped.AccountModel;
import com.thanlinardos.resource_server.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/myAccount")
    public ResponseEntity<AccountModel> getAccountDetails(@RequestParam String username) {
        return accountService.getAccountByCustomerEmail(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

package com.thanlinardos.resource_server.controller.rest;

import com.thanlinardos.resource_server.model.mapped.LoanModel;
import com.thanlinardos.resource_server.service.economy.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LoansController {

    private final LoanService loanService;

    @GetMapping("/myLoans")
    public ResponseEntity<List<LoanModel>> getLoansDetails(@RequestParam String email) {
        return ResponseEntity.ok(loanService.getLoansByPrincipalNameOrderByStartDtDesc(email));
    }
}

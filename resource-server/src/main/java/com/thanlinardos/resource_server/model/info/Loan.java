package com.thanlinardos.resource_server.model.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Loan implements Serializable {

    private LocalDate startDt;
    private String loanType;
    private Long totalLoan;
    private Long amountPaid;
    private Long outstandingAmount;
}

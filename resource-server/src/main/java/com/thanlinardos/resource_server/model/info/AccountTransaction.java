package com.thanlinardos.resource_server.model.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountTransaction implements Serializable {

    private UUID transactionId;
    private LocalDate transactionDt;
    private String transactionSummary;
    private String transactionType;
    private Long transactionAmt;
    private Long closingBalance;
}

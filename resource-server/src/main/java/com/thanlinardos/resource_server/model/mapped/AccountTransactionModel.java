package com.thanlinardos.resource_server.model.mapped;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thanlinardos.resource_server.model.OwnedResource;
import com.thanlinardos.resource_server.model.entity.AccountTransactionJpa;
import com.thanlinardos.resource_server.model.mapped.base.BasicIdModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class AccountTransactionModel extends BasicIdModel implements Serializable, OwnedResource<AccountTransactionModel> {

    private UUID transactionId;
    private Long accountId;
    private LocalDate transactionDt;
    private String transactionSummary;
    private String transactionType;
    private Long transactionAmt;
    private Long closingBalance;
    @ToString.Exclude
    @JsonIgnore
    private OwnerModel owner;

    public AccountTransactionModel(AccountTransactionJpa entity) {
        super(entity);
        this.transactionId = entity.getTransactionId();
        this.transactionDt = entity.getTransactionDt();
        this.transactionSummary = entity.getTransactionSummary();
        this.transactionType = entity.getTransactionType();
        this.transactionAmt = entity.getTransactionAmt();
        this.closingBalance = entity.getClosingBalance();
        this.accountId = entity.getAccount().getId();
        this.owner = new OwnerModel(entity.getAccount().getOwner());
    }
}

package com.thanlinardos.resource_server.model.entity;

import com.thanlinardos.resource_server.model.entity.base.BasicManyToOneAccountIdJpa;
import com.thanlinardos.resource_server.model.entity.base.IndirectlyOwnedEntity;
import com.thanlinardos.resource_server.model.mapped.AccountTransactionModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "account_transaction")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class AccountTransactionJpa extends BasicManyToOneAccountIdJpa implements IndirectlyOwnedEntity<AccountTransactionJpa> {

    private UUID transactionId;
    private LocalDate transactionDt;
    private String transactionSummary;
    private String transactionType;
    private Long transactionAmt;
    private Long closingBalance;
    @Transient
    @Getter
    private OwnerJpa owner;

    public static AccountTransactionJpa fromModel(AccountTransactionModel model) {
        return builder()
                .transactionId(model.getTransactionId())
                .transactionDt(model.getTransactionDt())
                .transactionSummary(model.getTransactionSummary())
                .transactionType(model.getTransactionType())
                .transactionAmt(model.getTransactionAmt())
                .closingBalance(model.getClosingBalance())
                .owner(OwnerJpa.fromModel(model.getOwner()))
                .build();
    }
}

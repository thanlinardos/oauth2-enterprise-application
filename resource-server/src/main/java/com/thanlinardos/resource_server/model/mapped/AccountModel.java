package com.thanlinardos.resource_server.model.mapped;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thanlinardos.resource_server.model.OwnedResource;
import com.thanlinardos.resource_server.model.entity.AccountJpa;
import com.thanlinardos.resource_server.model.entity.AccountTransactionJpa;
import com.thanlinardos.resource_server.model.entity.CardJpa;
import com.thanlinardos.spring_enterprise_library.model.mapped.base.BasicIdModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class AccountModel extends BasicIdModel implements Serializable, OwnedResource<AccountModel> {

    @ToString.Exclude
    @JsonIgnore
    private OwnerModel owner;
    private List<AccountTransactionModel> accountTransactions;
    private Long accountNumber;
    private String accountType;
    private String branchAddress;
    private List<CardModel> cards;

    public AccountModel(AccountJpa entity) {
        super(entity);
        this.setAccountNumber(entity.getAccountNumber());
        this.setAccountType(entity.getAccountType());
        this.setBranchAddress(entity.getBranchAddress());
        this.setOwner(new OwnerModel(entity.getOwner()));
    }

    public void setAccountTransactionJpas(List<AccountTransactionJpa> entities) {
        this.setAccountTransactions(entities.stream()
                .map(AccountTransactionModel::new)
                .toList());
    }

    public void setCardJpas(List<CardJpa> entities) {
        this.setCards(entities.stream()
                .map(CardModel::new)
                .toList());
    }
}

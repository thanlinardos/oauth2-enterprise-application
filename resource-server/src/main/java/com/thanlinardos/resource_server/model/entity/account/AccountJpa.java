package com.thanlinardos.resource_server.model.entity.account;

import com.thanlinardos.resource_server.model.entity.owner.OwnerJpa;
import com.thanlinardos.resource_server.model.entity.base.BasicOneToOneOwnedAuditableJpa;
import com.thanlinardos.resource_server.model.mapped.AccountModel;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils.EntityUtils;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "account")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class AccountJpa extends BasicOneToOneOwnedAuditableJpa {

    @OneToMany(mappedBy = "account",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @ToString.Exclude
    @Builder.Default
    private List<AccountTransactionJpa> accountTransactions = new ArrayList<>();
    @OneToMany(mappedBy = "account",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @ToString.Exclude
    @Builder.Default
    private List<CardJpa> cards = new ArrayList<>();
    private long accountNumber;
    private String accountType;
    private String branchAddress;

    /**
     * Adds an AccountTransactionJpa to the accountTransactions list and sets the account reference in the AccountTransactionJpa.
     *
     * @param accountTransaction the AccountTransactionJpa to be added.
     */
    public void addAccountTransactionWithLink(AccountTransactionJpa accountTransaction) {
        EntityUtils.addMemberWithLink(this, accountTransaction, accountTransaction::setAccount, accountTransactions);
    }

    /**
     * Adds a CardJpa to the cards list and sets the account reference in the CardJpa.
     *
     * @param card the CardJpa to be added.
     */
    public void addCardWithLink(CardJpa card) {
        EntityUtils.addMemberWithLink(this, card, card::setAccount, cards);
    }

    public static AccountJpa fromModel(AccountModel accountModel) {
        List<AccountTransactionJpa> accountTransactions = accountModel.getAccountTransactions().stream()
                .map(AccountTransactionJpa::fromModel)
                .toList();
        return builder()
                .accountNumber(accountModel.getAccountNumber())
                .accountType(accountModel.getAccountType())
                .branchAddress(accountModel.getBranchAddress())
                .accountTransactions(accountTransactions)
                .cards(accountModel.getCards().stream()
                        .map(CardJpa::fromModel)
                        .toList())
                .owner(OwnerJpa.fromModel(accountModel.getOwner()))
                .build();
    }
}

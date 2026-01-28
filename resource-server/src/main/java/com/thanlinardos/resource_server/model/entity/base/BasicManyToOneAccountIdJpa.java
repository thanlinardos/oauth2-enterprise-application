package com.thanlinardos.resource_server.model.entity.base;

import com.thanlinardos.resource_server.model.entity.account.AccountJpa;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public class BasicManyToOneAccountIdJpa extends BasicIdJpa {

    @ManyToOne(targetEntity = AccountJpa.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    @ToString.Exclude
    private AccountJpa account;
}

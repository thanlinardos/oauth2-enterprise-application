package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.account.AccountJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountJpa, Long> {

    Optional<AccountJpa> findByAccountNumber(long accountNumber);

    Optional<AccountJpa> findByOwner_Name(String ownerName);
}

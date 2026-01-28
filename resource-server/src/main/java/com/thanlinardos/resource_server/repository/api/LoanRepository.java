package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.economy.LoanJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<LoanJpa, Long> {

    List<LoanJpa> getByOwnerNameOrderByStartDtDesc(String name);
}

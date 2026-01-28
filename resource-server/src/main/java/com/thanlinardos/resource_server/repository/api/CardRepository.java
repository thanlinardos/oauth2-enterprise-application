package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.account.CardJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<CardJpa, Long> {

    List<CardJpa> getByAccountOwnerName(String name);
}

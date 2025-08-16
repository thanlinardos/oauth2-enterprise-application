package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.model.entity.LoanJpa;
import com.thanlinardos.resource_server.model.mapped.LoanModel;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final EntityManager entityManager;

    @Transactional
    public List<LoanModel> getLoansByPrincipalNameOrderByStartDtDesc(String name) {
        return entityManager
                .createQuery("from LoanJpa a join fetch a.owner o join fetch o.roles where o.name = :name order by a.startDt desc", LoanJpa.class)
                .setParameter("name", name)
                .getResultStream()
                .map(LoanModel::new)
                .toList();
    }
}

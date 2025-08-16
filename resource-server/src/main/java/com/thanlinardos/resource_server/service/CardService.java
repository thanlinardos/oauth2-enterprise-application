package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.model.entity.CardJpa;
import com.thanlinardos.resource_server.model.mapped.CardModel;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final EntityManager entityManager;

    public List<CardModel> getCardsDetails() {
        return getCardsForPrincipalName(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private List<CardModel> getCardsForPrincipalName(String name) {
        return entityManager.createQuery("SELECT c FROM CardJpa c where c.account.owner.name=:name", CardJpa.class)
                .setParameter("name", name)
                .getResultList().stream()
                .map(CardModel::new)
                .toList();
    }
}
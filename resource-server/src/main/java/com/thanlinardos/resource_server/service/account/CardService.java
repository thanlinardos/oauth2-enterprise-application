package com.thanlinardos.resource_server.service.account;

import com.thanlinardos.resource_server.model.mapped.CardModel;
import com.thanlinardos.resource_server.repository.api.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public List<CardModel> getCardsDetails() {
        return getCardsForPrincipalName(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private List<CardModel> getCardsForPrincipalName(String name) {
        return cardRepository.getByAccountOwnerName(name).stream()
                .map(CardModel::new)
                .toList();
    }
}
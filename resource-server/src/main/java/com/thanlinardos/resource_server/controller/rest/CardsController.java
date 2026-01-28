package com.thanlinardos.resource_server.controller.rest;

import com.thanlinardos.resource_server.model.mapped.CardModel;
import com.thanlinardos.resource_server.service.account.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CardsController {

    private final CardService cardService;

    @GetMapping("/myCards")
    public List<CardModel> getCardsDetails() {
        return cardService.getCardsDetails();
    }
}

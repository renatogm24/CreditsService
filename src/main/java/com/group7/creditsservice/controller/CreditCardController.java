package com.group7.creditsservice.controller;

import com.group7.creditsservice.entity.CreditCard;
import com.group7.creditsservice.service.ICreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("credit_cards")

public class CreditCardController {
    @Autowired
    private ICreditCardService service;

    @GetMapping
    public Flux<CreditCard> getCreditCards() {
        return service.findAllCreditCars();
    }

    @PostMapping
    public Mono<CreditCard> saveCreditCard(@RequestBody CreditCard creditCard) {
        return service.saveCreditCard(creditCard);
    }
}
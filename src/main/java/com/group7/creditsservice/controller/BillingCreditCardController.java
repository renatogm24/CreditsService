package com.group7.creditsservice.controller;

import com.group7.creditsservice.dto.BillingRequest;
import com.group7.creditsservice.dto.BillingResponse;
import com.group7.creditsservice.dto.MovementResponse;
import com.group7.creditsservice.repository.BillingCreditCardRepository;
import com.group7.creditsservice.service.BillingCreditCardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/credits/credit_cards/billing")
@AllArgsConstructor
@Slf4j
public class BillingCreditCardController {
    private BillingCreditCardService service;

    @GetMapping("{credit}")
    public Flux<BillingResponse> getAllBillingByCredit(@PathVariable String credit) {
        return service.getBillingByCredit(credit);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BillingResponse> saveBilling(@Valid @RequestBody BillingRequest billingRequest) {
        return service.save(billingRequest);
    }
}

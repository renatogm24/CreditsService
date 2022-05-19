package com.group7.creditsservice.controller;

import com.group7.creditsservice.dto.CreditCardRequest;
import com.group7.creditsservice.dto.CreditCardResponse;
import com.group7.creditsservice.model.CreditCard;
import com.group7.creditsservice.service.CreditCardService;
import com.group7.creditsservice.service.MovementCreditCardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/credits/credit_cards")
@AllArgsConstructor
@Slf4j
public class CreditCardController {
    private CreditCardService service;
    private MovementCreditCardService movementService;

    @GetMapping
    public Flux<CreditCard> getCreditCards() {
        return service.findAllCreditCars();
    }

    @GetMapping("{id}")
    public Mono<CreditCardResponse> getCreditCard(@PathVariable final String id) {
        return service.getById(id);
    }

    @SuppressWarnings({"checkstyle:MissingJavadocMethod", "checkstyle:LineLength"})
    @GetMapping("/client/{client}")
    public Flux<CreditCardResponse> getAllCreditCardsByClient(@PathVariable final String client) {
        return service.getAllCreditCardsByClient(client);
    }

    @GetMapping("/client/{client}/report_average_daily_balance")
    public Flux<Map<String, Double>> getAverageDailyBalance(@PathVariable final String client) {

        return movementService.getAllReportsByClient(client);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CreditCardResponse> saveCreditCard(@Valid @RequestBody CreditCardRequest creditCardRequestMono) {
        return service.saveCreditCard(creditCardRequestMono);
    }

    @PutMapping("{id}")
    public Mono<CreditCardResponse> updateCreditCard(@PathVariable String id, @Valid @RequestBody Mono<CreditCardRequest> creditCardRequestMono) {
        return service.updateCreditCard(id, creditCardRequestMono);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteCreditCard(@PathVariable final String id) {
        return service.deleteCreditCard(id);
    }
}

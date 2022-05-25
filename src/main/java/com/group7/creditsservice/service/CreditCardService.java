package com.group7.creditsservice.service;

import com.group7.creditsservice.dto.CreditCardRequest;
import com.group7.creditsservice.dto.CreditCardResponse;
import com.group7.creditsservice.dto.CreditReportResponse;
import com.group7.creditsservice.model.CreditCard;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface CreditCardService {

    Flux<CreditCardResponse> findAllCreditCars();

    Mono<CreditCardResponse> getById(String id);

    Flux<CreditCardResponse> getAllCreditCardsByClient(String client);

    Mono<CreditReportResponse> getReport(String id, LocalDate from, LocalDate to);

    Mono<CreditCardResponse> saveCreditCard(CreditCardRequest creditCardRequestMono);

    Mono<CreditCardResponse> updateCreditCard(String id, CreditCardRequest creditCardRequest);

    Mono<Void> deleteCreditCard(String id);
}

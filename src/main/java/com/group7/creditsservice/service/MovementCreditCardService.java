package com.group7.creditsservice.service;

import com.group7.creditsservice.dto.MovementRequest;
import com.group7.creditsservice.dto.MovementResponse;
import com.group7.creditsservice.model.MovementCreditCard;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface MovementCreditCardService {

    Flux<MovementCreditCard> getAll();

    Mono<MovementCreditCard> getById(String id);

    Flux<MovementResponse> getAllMovementsByCredit(String credit);
    Flux<MovementResponse> getLatestMovementsByCredit(String credit);

    Mono<Double> getStateByCreditPerMonthAndYear(String credit, int year, int month);
    Mono<Double> getAverageDailyBalance(String credit);

    Flux<Map<String, Double>> getAllReportsByClient(String client);

    Mono<MovementResponse> save(MovementRequest movementRequest);

    Mono<Void> delete(String id);

    Mono<Void> deleteAll();

}

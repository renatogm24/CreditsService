package com.group7.creditsservice.service;

import com.group7.creditsservice.dto.MovementRequest;
import com.group7.creditsservice.dto.MovementResponse;
import com.group7.creditsservice.model.MovementLoan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementLoanService {
    Flux<MovementLoan> getAll();

    Mono<MovementLoan> getById(String id);

    Flux<MovementResponse> getAllMovementsByLoan(String loan);

    Mono<Double> getStateByLoanPerMonthAndYear(String loan, int year, int month);

    Mono<Void> delete(String id);

    Mono<Void> deleteAll();

    Mono<MovementResponse> save(MovementRequest movementRequest);

    Mono<MovementLoan> update(String id, MovementLoan movementLoan);

}

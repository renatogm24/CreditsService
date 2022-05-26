package com.group7.creditsservice.controller;

import com.group7.creditsservice.dto.MovementRequest;
import com.group7.creditsservice.dto.MovementResponse;
import com.group7.creditsservice.service.MovementLoanService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/credits/loans/movement")
@AllArgsConstructor
@Slf4j

public class MovementLoanController {

    private MovementLoanService service;

    @GetMapping
    public Flux<MovementResponse> getAllMovements() {
        return service.getAll();
    }

    @GetMapping("/product/{loan}")
    public Flux<MovementResponse> getAllMovementByLoan(@PathVariable String loan) {
        return service.getAllMovementsByLoan(loan);
    }

    @GetMapping("/product/{loan}/state/{year}/{month}")
    public Mono<Double> getStateByLoanPerMonthAndYear(@PathVariable String loan, @PathVariable int year, @PathVariable int month) {
        return service.getStateByLoanPerMonthAndYear(loan, year, month);
    }

    @GetMapping("{id}")
    public Mono<MovementResponse> getMovement(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovementResponse> saveMovementLoan(@Valid @RequestBody MovementRequest movementRequest) {
        return service.save(movementRequest);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteMovement(@PathVariable String id) {
        return service.delete(id);
    }

    @DeleteMapping
    public Mono<Void> deleteAllMovements() {
        return service.deleteAll();
    }
}

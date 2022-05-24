package com.group7.creditsservice.controller;

import com.group7.creditsservice.dto.MovementRequest;
import com.group7.creditsservice.dto.MovementResponse;
import com.group7.creditsservice.model.MovementCreditCard;
import com.group7.creditsservice.service.MovementCreditCardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/credits/credit_cards/movement")
@AllArgsConstructor
@Slf4j
public class MovementCreditCardController {

    private MovementCreditCardService service;

    @GetMapping
    public Flux<MovementCreditCard> getAllMovements(){
        return service.getAll();
    }

    @GetMapping("/product/{credit}")
    public Flux<MovementResponse> getAllMovementsByCredit(@PathVariable String credit){
        return service.getAllMovementsByCredit(credit);
    }

    @GetMapping("latest/{credit}")
    public Flux<MovementResponse> getLastestMovementsByCredit(@PathVariable String credit) {
        return service.getLatestMovementsByCredit(credit);
    }

    @GetMapping("/product/{credit}/state/{year}/{month}")
    public Mono<Double> getStateByCreditPerMonthAndYear(@PathVariable String credit, @PathVariable int year, @PathVariable int month){
        return service.getStateByCreditPerMonthAndYear(credit, year, month);
    }

    @GetMapping("{id}")
    public Mono<MovementCreditCard> getMovement(@PathVariable String id){
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovementResponse> saveMovement(@Valid @RequestBody MovementRequest movementRequest){
        return service.save(movementRequest);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteMovement(@PathVariable String id){
        return service.delete(id);
    }

    @DeleteMapping
    public Mono<Void> deleteAllMovements(){
        return service.deleteAll();
    }

}

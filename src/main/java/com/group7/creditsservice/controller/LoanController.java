package com.group7.creditsservice.controller;

import com.group7.creditsservice.dto.LoanRequest;
import com.group7.creditsservice.dto.LoanResponse;
import com.group7.creditsservice.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/credits/loans")

public class LoanController {
    @Autowired
    private LoanService service;

    @GetMapping
    public Flux<LoanResponse> getLoans() {
        return service.findAllLoans();
    }

    @GetMapping("/client/{client}")
    public Flux<LoanResponse> getAllLoansByClient(@PathVariable final String client) {
        return service.getAllLoansByClient(client);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<LoanResponse> saveLoan(@Valid @RequestBody Mono<LoanRequest> loanRequest) {
        return service.saveLoan(loanRequest);
    }

    @PutMapping("{id}")
    public Mono<LoanResponse> updateLoan(@PathVariable String id, @Valid @RequestBody LoanRequest loanRequest) {
        return service.update(id, loanRequest);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteLoan(@PathVariable String id) {
        return service.delete(id);
    }
}

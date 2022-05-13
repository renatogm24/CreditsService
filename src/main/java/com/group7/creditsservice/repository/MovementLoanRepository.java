package com.group7.creditsservice.repository;

import com.group7.creditsservice.model.MovementLoan;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.Date;

public interface MovementLoanRepository extends ReactiveMongoRepository<MovementLoan, String> {
    Flux<MovementLoan> findByLoan(String loan);

    Flux<MovementLoan> findByLoanAndDateBetween(String loan, Date from, Date to);
}
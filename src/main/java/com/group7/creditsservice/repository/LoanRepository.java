package com.group7.creditsservice.repository;

import com.group7.creditsservice.model.Loan;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface LoanRepository extends ReactiveMongoRepository<Loan, String> {
    Flux<Loan> findLoansByClient(String client);
}

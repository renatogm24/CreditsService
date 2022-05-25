package com.group7.creditsservice.service;

import com.group7.creditsservice.dto.LoanRequest;
import com.group7.creditsservice.dto.LoanResponse;
import com.group7.creditsservice.exception.credit.CreditNotFoundException;
import com.group7.creditsservice.model.Loan;
import com.group7.creditsservice.repository.LoanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class LoanServiceImpl implements LoanService {
    @Autowired
    private LoanRepository repository;

    @Override
    public Flux<Loan> findAllLoans() {
        return repository.findAll();
    }

    @Override
    public Flux<LoanResponse> getAllLoansByClient(final String client) {
        return repository.findLoansByClient(client)
                .map(LoanResponse::fromModel);
    }

    @Override
    public Mono<LoanResponse> saveLoan(Mono<LoanRequest> loanRequest) {
        return loanRequest.map(LoanRequest::toModel)
                .flatMap(repository::insert)
                .map(LoanResponse::fromModel);
    }

    @Override
    public Mono<LoanResponse> update(String id, Mono<LoanRequest> loanRequest) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new CreditNotFoundException("Loan not found with id:"+id)))
                .doOnError(ex -> log.error("Loan not found with id: {}", id, ex))
                .flatMap(loan -> loanRequest.map(LoanRequest::toModel))
                .map(LoanResponse::fromModel)
                .doOnSuccess(res -> log.info("Updated loan with ID:", res.getId()));
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new CreditNotFoundException("Loan not found with id:"+id)))
                .doOnError(ex->log.error("Loan not found with id: {}", id, ex))
                .flatMap(existingLoan -> repository.delete(existingLoan)
                        ).doOnSuccess(ex ->log.info("Delete loan with id: {}", id));
    }

}

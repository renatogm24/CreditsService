package com.group7.creditsservice.serviceimpl;

import com.group7.creditsservice.dto.LoanRequest;
import com.group7.creditsservice.dto.LoanResponse;
import com.group7.creditsservice.exception.billing.BillingCreationException;
import com.group7.creditsservice.exception.credit.CreditCreationException;
import com.group7.creditsservice.exception.credit.CreditNotFoundException;
import com.group7.creditsservice.model.Loan;
import com.group7.creditsservice.repository.LoanRepository;
import com.group7.creditsservice.service.LoanService;
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
    public Flux<LoanResponse> findAllLoans() {
        return repository.findAll().map(LoanResponse::fromModel);
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
                .map(LoanResponse::fromModel)
                .onErrorMap(ex -> new CreditCreationException(ex.getMessage()));
    }

    @Override
    public Mono<LoanResponse> update(String id, LoanRequest loanRequest) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new CreditNotFoundException("Loan not found with id:"+id)))
                .doOnError(ex -> log.error("Loan not found with id: {}", id, ex))
                .flatMap(loan -> {
                    loan.setPaymentDay(loanRequest.getPaymentDay());
                    return repository.save(loan);
                })
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

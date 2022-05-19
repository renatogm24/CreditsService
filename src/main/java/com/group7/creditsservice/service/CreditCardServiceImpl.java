package com.group7.creditsservice.service;

import com.group7.creditsservice.dto.CreditCardRequest;
import com.group7.creditsservice.dto.CreditCardResponse;
import com.group7.creditsservice.exception.loan.LoanNotFoundException;
import com.group7.creditsservice.model.CreditCard;
import com.group7.creditsservice.repository.CreditCardRepository;
import com.group7.creditsservice.utils.WebClientUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class CreditCardServiceImpl implements CreditCardService {

    @Autowired
    private CreditCardRepository repository;

    private WebClientUtils webClientUtils;

    @Override
    public Flux<CreditCard> findAllCreditCars() {
        return repository.findAll().switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<CreditCardResponse> getById(final String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new LoanNotFoundException("Not found credit card" + id)))
                .doOnError(ex -> log.error("Not found credit card", id, ex))
                .map(CreditCardResponse::fromModel);
    }

    @Override
    public Flux<CreditCardResponse> getAllCreditCardsByClient(final String client) {
        return repository.findCreditCardsByClient(client)
                .map(CreditCardResponse::fromModel);
    }

    @Override
    public Mono<CreditCardResponse> saveCreditCard(CreditCardRequest creditCardRequest) {
        return Mono.just(creditCardRequest)
                .map(CreditCardRequest::toModel)
                .flatMap(creditCardRequest1 -> webClientUtils.getClient(creditCardRequest1.getClient())
                        .switchIfEmpty(Mono.error(new LoanNotFoundException("Client not found: " + creditCardRequest1.getClient())))
                        .doOnError(ex -> log.error("Client not found" + creditCardRequest1.getClient()))
                        .flatMap(res -> repository.save(creditCardRequest1))
                )
                .map(CreditCardResponse::fromModel);

    }

    @Override
    public Mono<CreditCardResponse> updateCreditCard(final String id, Mono<CreditCardRequest> creditCardRequestMono) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new LoanNotFoundException("Credit card not found with id: "+ id)))
                .doOnError(ex -> log.error("Credit card not found with id: {}", id, ex))
                .flatMap(creditCard -> creditCardRequestMono.map(CreditCardRequest::toModel))
                .map(CreditCardResponse::fromModel)
                .doOnSuccess(res -> log.info("Updated credit card with id", res.getId()));
    }

    @Override
    public Mono<Void> deleteCreditCard(final String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new LoanNotFoundException("Credit card not found with id: " + id)))
                .doOnError(ex -> log.error("Credit card found with id: {}", id, ex))
                .flatMap(existing -> repository.delete(existing))
                        .doOnSuccess(ex -> log.info("Delete credit card with id {}", id));
    }
}

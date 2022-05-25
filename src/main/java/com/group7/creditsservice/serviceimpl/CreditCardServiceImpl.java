package com.group7.creditsservice.serviceimpl;

import com.group7.creditsservice.dto.*;
import com.group7.creditsservice.exception.credit.CreditNotFoundException;
import com.group7.creditsservice.model.CreditCard;
import com.group7.creditsservice.repository.BillingCreditCardRepository;
import com.group7.creditsservice.repository.CreditCardRepository;
import com.group7.creditsservice.repository.MovementCreditCardRepository;
import com.group7.creditsservice.service.CreditCardService;
import com.group7.creditsservice.utils.WebClientUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CreditCardServiceImpl implements CreditCardService {

    @Autowired
    private CreditCardRepository repository;

    private MovementCreditCardRepository movementCreditCardRepository;
    private BillingCreditCardRepository billingCreditCardRepository;

    private WebClientUtils webClientUtils;

    @Override
    public Flux<CreditCard> findAllCreditCars() {
        return repository.findAll().switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<CreditCardResponse> getById(final String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new CreditNotFoundException("Not found credit card" + id)))
                .doOnError(ex -> log.error("Not found credit card {}", id, ex))
                .map(CreditCardResponse::fromModel);
    }

    @Override
    public Flux<CreditCardResponse> getAllCreditCardsByClient(final String client) {
        return repository.findCreditCardsByClient(client)
                .map(CreditCardResponse::fromModel);
    }

    @Override
    public Mono<CreditReportResponse> getReport(String id, LocalDate from, LocalDate to) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new CreditNotFoundException("Not found credit card "+id)))
                .doOnError(ex -> log.error("Not found credit card {}", id, ex))
                .flatMap(creditCard -> {
                    CreditReportResponse report = CreditReportResponse.fromModel(creditCard);

                    Mono<List<BillingResponse>> billings = billingCreditCardRepository.findByCreditAndBillingDateBetween(id, from, to)
                            .map(BillingResponse::fromModelBillingCreditCard)
                            .collectList();

                    Mono<List<MovementResponse>> movements = movementCreditCardRepository.findByCreditAndDateBetween(id, from, to)
                            .map(MovementResponse::fromModelMovementCreditCard)
                            .collectList();

                    return Mono.zip(billings, movements)
                            .map(result -> {
                                report.setBillings(result.getT1());
                                report.setMovements(result.getT2());
                                return report;
                            })
                            .doOnError(err->log.error("Error", err));
                });
    }

    @Override
    public Mono<CreditCardResponse> saveCreditCard(CreditCardRequest creditCardRequest) {
        return Mono.just(creditCardRequest)
                .map(CreditCardRequest::toModel)
                .flatMap(creditCardRequest1 -> webClientUtils.getClient(creditCardRequest1.getClient())
                        .switchIfEmpty(Mono.error(new CreditNotFoundException("Client not found: " + creditCardRequest1.getClient())))
                        .doOnError(ex -> log.error("Client not found" + creditCardRequest1.getClient()))
                        .flatMap(res -> repository.save(creditCardRequest1))
                )
                .map(CreditCardResponse::fromModel);

    }

    @Override
    public Mono<CreditCardResponse> updateCreditCard(final String id, Mono<CreditCardRequest> creditCardRequestMono) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new CreditNotFoundException("Credit card not found with id: "+ id)))
                .doOnError(ex -> log.error("Credit card not found with id: {}", id, ex))
                .flatMap(creditCard -> creditCardRequestMono.map(CreditCardRequest::toModel))
                .map(CreditCardResponse::fromModel)
                .doOnSuccess(res -> log.info("Updated credit card with id", res.getId()));
    }

    @Override
    public Mono<Void> deleteCreditCard(final String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new CreditNotFoundException("Credit card not found with id: " + id)))
                .doOnError(ex -> log.error("Credit card found with id: {}", id, ex))
                .flatMap(existing -> repository.delete(existing))
                        .doOnSuccess(ex -> log.info("Delete credit card with id {}", id));
    }
}

package com.group7.creditsservice.repository;

import com.group7.creditsservice.model.BillingCreditCard;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface BillingCreditCardRepository extends ReactiveMongoRepository<BillingCreditCard, String> {
    Mono<BillingCreditCard> findByCreditAndActiveIsTrue(String credit);
    Flux<BillingCreditCard> findByCredit(String credit);
    Mono<Boolean> existsByClientAndMinPaymentGreaterThanAndLastDayPaymentLessThan(String client, int minPayment, LocalDate date);

    Flux<BillingCreditCard> findByCreditAndBillingDateBetween(String credit, LocalDate from, LocalDate to);
}

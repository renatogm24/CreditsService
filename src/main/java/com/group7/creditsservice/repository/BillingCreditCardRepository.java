package com.group7.creditsservice.repository;

import com.group7.creditsservice.model.BillingCreditCard;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BillingCreditCardRepository extends ReactiveMongoRepository<BillingCreditCard, String> {
    Flux<BillingCreditCard> findByActiveIsTrue();

    Mono<Boolean> existsByActive(Boolean active);
}

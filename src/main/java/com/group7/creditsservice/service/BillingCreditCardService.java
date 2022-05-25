package com.group7.creditsservice.service;

import com.group7.creditsservice.dto.BillingRequest;
import com.group7.creditsservice.dto.BillingResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BillingCreditCardService {

    Flux<BillingResponse> getBillingByCredit(String credit);

    Mono<Boolean> isDebt(String client);
    Mono<BillingResponse> save(BillingRequest billingRequest);

    Mono<BillingResponse> disableBilling(String id);
}

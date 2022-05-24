package com.group7.creditsservice.service;

import com.group7.creditsservice.dto.BillingRequest;
import com.group7.creditsservice.dto.BillingResponse;
import com.group7.creditsservice.dto.MovementResponse;
import com.group7.creditsservice.exception.billing.BillingCreationException;
import com.group7.creditsservice.repository.BillingCreditCardRepository;
import com.group7.creditsservice.repository.CreditCardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class BillingCreditCardServiceImpl implements BillingCreditCardService {

    private BillingCreditCardRepository billingCreditCardRepository;
    private CreditCardRepository creditCardRepository;

    @Override
    public Flux<BillingResponse> getBillingByCredit(String credit) {
        return billingCreditCardRepository.findByActiveIsTrue().map(BillingResponse::fromModelBillingCreditCard);
    }

    @Override
    public Mono<BillingResponse> save(BillingRequest billingRequest) {
        return Mono.just(billingRequest)
                .map(BillingRequest::toModelBillingCreditCard)
                .flatMap(billingCreditCard -> creditCardRepository.findById(billingRequest.getCredit())
                        .switchIfEmpty(Mono.error(new BillingCreationException("Credit card not found with id: "+billingCreditCard.getCredit())))
                        .flatMap(billingCreditCard1 -> {
                            return billingCreditCardRepository.findByActiveIsTrue()
                                    .hasElements()
                                    .flatMap(hasElement -> {
                                        if (hasElement) {
                                            return Mono.error(new BillingCreationException("There is an activated billing date"));
                                        }

                                        return billingCreditCardRepository.save(billingCreditCard);
                                    });
                        }))
                .map(BillingResponse::fromModelBillingCreditCard);
    }
}

package com.group7.creditsservice.serviceimpl;

import com.group7.creditsservice.dto.BillingRequest;
import com.group7.creditsservice.dto.BillingResponse;
import com.group7.creditsservice.exception.billing.BillingCreationException;
import com.group7.creditsservice.repository.BillingCreditCardRepository;
import com.group7.creditsservice.repository.CreditCardRepository;
import com.group7.creditsservice.service.BillingCreditCardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@AllArgsConstructor
@Slf4j
public class BillingCreditCardServiceImpl implements BillingCreditCardService {

    private BillingCreditCardRepository billingCreditCardRepository;
    private CreditCardRepository creditCardRepository;

    @Override
    public Flux<BillingResponse> getBillingByCredit(String credit) {
        return billingCreditCardRepository.findByCredit(credit).map(BillingResponse::fromModelBillingCreditCard);
    }

    @Override
    public Mono<Boolean> isDebt(String client) {
        return billingCreditCardRepository.existsByClientAndMinPaymentGreaterThanAndLastDayPaymentLessThan(client, 0, LocalDate.now());
    }

    @Override
    public Mono<BillingResponse> save(BillingRequest billingRequest) {
        return Mono.just(billingRequest)
                .map(BillingRequest::toModelBillingCreditCard)
                .flatMap(billingCreditCard -> creditCardRepository.findById(billingRequest.getCredit())
                        .switchIfEmpty(Mono.error(new BillingCreationException("Credit card not found with id: "+billingCreditCard.getCredit())))
                        .flatMap(billingCreditCard1 -> billingCreditCardRepository.findByCreditAndActiveIsTrue(billingCreditCard.getCredit())
                            .hasElement()
                            .flatMap(hasElement -> {
                                if (hasElement) {
                                    return Mono.error(new BillingCreationException("There is an activated billing date"));
                                }

                                billingCreditCard.setClient(billingCreditCard1.getClient());
                                return billingCreditCardRepository.save(billingCreditCard);
                            })
                        ))
                .map(BillingResponse::fromModelBillingCreditCard)
                .onErrorMap(ex -> new BillingCreationException(ex.getMessage()))
                .doOnError(ex -> log.error("Error save", ex));
    }

    @Override
    public Mono<BillingResponse> disableBilling(String id) {
        return billingCreditCardRepository.findById(id)
                .switchIfEmpty(Mono.error(new BillingCreationException("Billing credit card not found with id: "+id)))
                .flatMap(billingCreditCard -> {
                    billingCreditCard.setActive(false);
                    return billingCreditCardRepository.save(billingCreditCard);
                })
                .map(BillingResponse::fromModelBillingCreditCard);
    }
}

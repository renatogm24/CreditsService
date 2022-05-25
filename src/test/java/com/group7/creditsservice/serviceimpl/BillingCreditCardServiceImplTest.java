package com.group7.creditsservice.serviceimpl;

import com.group7.creditsservice.dto.BillingRequest;
import com.group7.creditsservice.dto.BillingResponse;
import com.group7.creditsservice.dto.CreditCardResponse;
import com.group7.creditsservice.model.BillingCreditCard;
import com.group7.creditsservice.model.CreditCard;
import com.group7.creditsservice.repository.BillingCreditCardRepository;
import com.group7.creditsservice.repository.CreditCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

public class BillingCreditCardServiceImplTest {
    private static final String CREDIT_CARD_ID = "62784dd906dc7f2ed86d12d5";
    private static final String BILLING_CREDIT_CARD_ID = "628dcd1977d2b639fd72b9f5";
    private static final LocalDate BILLING_CREDIT_CARD_LAST_PAYMENT_DAY = LocalDate.of(2022, 5, 25) ;
    private static final LocalDate BILLING_CREDIT_CARD_BILLING_DATE =  LocalDate.of(2022, 5, 10);
    private static final Double BILLING_CREDIT_CARD_MIN_PAYMENT = 95.0;
    private static final Double BILLING_CREDIT_CARD_FULL_PAYMENT = 950.0;

    private static final String CREDIT_CARD_CLIENT = "62784dd906dc7f2ed86d12d5";
    private static final Integer CREDIT_CARD_PAYMENT_DAY = 20;
    private static final String CREDIT_CARD_NUMBER = "4213-1212-12122-1212";
    private static final Integer CREDIT_CARD_BILLING_DAY = 10;
    private static final Double CREDIT_CARD_AMOUNT = 1000.0;
    private static final Double CREDIT_CARD_BALANCE = 1000.0;
    private static final Double CREDIT_CARD_TCEA = 10.5;

    @Mock
    private BillingCreditCardRepository billingCreditCardRepository;

    @Mock
    private CreditCardRepository creditCardRepository;

    @InjectMocks
    private BillingCreditCardServiceImpl billingCreditCardService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBillingByCredit() {
        BillingCreditCard billingCreditCard = BillingCreditCard.builder()
            .id(BILLING_CREDIT_CARD_ID)
            .credit(CREDIT_CARD_ID)
            .client(CREDIT_CARD_CLIENT)
            .lastDayPayment(BILLING_CREDIT_CARD_LAST_PAYMENT_DAY)
            .billingDate(BILLING_CREDIT_CARD_BILLING_DATE)
            .minPayment(BILLING_CREDIT_CARD_MIN_PAYMENT)
            .fullPayment(BILLING_CREDIT_CARD_FULL_PAYMENT)
            .active(true)
            .build();

        when(billingCreditCardRepository.findByCredit(CREDIT_CARD_ID))
                .thenReturn(Flux.just(billingCreditCard));

        StepVerifier.create(billingCreditCardService.getBillingByCredit(CREDIT_CARD_ID))
                .expectNext(BillingResponse.fromModelBillingCreditCard(billingCreditCard))
                .verifyComplete();
    }

    @Test
    void isDebt() {
        when(billingCreditCardRepository.existsByClientAndMinPaymentGreaterThanAndLastDayPaymentLessThan(CREDIT_CARD_CLIENT, 0, LocalDate.now()))
                .thenReturn(Mono.just(false));

        StepVerifier.create(billingCreditCardService.isDebt(CREDIT_CARD_CLIENT))
                .expectNext(false)
                .verifyComplete();
    }
}

package com.group7.creditsservice.serviceimpl;

import com.group7.creditsservice.dto.MovementRequest;
import com.group7.creditsservice.dto.MovementResponse;
import com.group7.creditsservice.model.BillingCreditCard;
import com.group7.creditsservice.model.CreditCard;
import com.group7.creditsservice.model.Movement;
import com.group7.creditsservice.model.MovementCreditCard;
import com.group7.creditsservice.repository.BillingCreditCardRepository;
import com.group7.creditsservice.repository.CreditCardRepository;
import com.group7.creditsservice.repository.MovementCreditCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MovementCreditServiceImplTest {
    private static final String MOVEMENT_CREDIT_CARD_ID = "628ea1dd628a580f7d446e9e";
    private static final String CREDIT_CARD_ID = "628ea0f3628a580f7d446e9d";
    private static final String MOVEMENT_CREDIT_CARD_TYPE = "withdraw";
    private static final Double MOVEMENT_CREDIT_CARD_AMOUNT = 1.0;

    private static final String CREDIT_CARD_CLIENT = "62784dd906dc7f2ed86d12d5";
    private static final Integer CREDIT_CARD_PAYMENT_DAY = 20;
    private static final String CREDIT_CARD_NUMBER = "4213-1212-12122-1212";
    private static final Integer CREDIT_CARD_BILLING_DAY = 10;
    private static final Double CREDIT_CARD_AMOUNT = 1000.0;
    private static final Double CREDIT_CARD_BALANCE = 1000.0;
    private static final Double CREDIT_CARD_TCEA = 10.5;

    private static final String BILLING_CREDIT_CARD_ID = "628dcd1977d2b639fd72b9f5";
    private static final LocalDate BILLING_CREDIT_CARD_LAST_PAYMENT_DAY = LocalDate.of(2022, 5, 25) ;
    private static final LocalDate BILLING_CREDIT_CARD_BILLING_DATE =  LocalDate.of(2022, 5, 10);
    private static final Double BILLING_CREDIT_CARD_MIN_PAYMENT = 95.0;
    private static final Double BILLING_CREDIT_CARD_FULL_PAYMENT = 950.0;



    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private BillingCreditCardRepository billingCreditCardRepository;

    @Mock
    private MovementCreditCardRepository movementCreditCardRepository;

    @InjectMocks
    private MovementCreditCardServiceImpl movementCreditCardService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll() {
        MovementCreditCard movementCreditCard = MovementCreditCard.builder()
                .id(MOVEMENT_CREDIT_CARD_ID)
                .type(MOVEMENT_CREDIT_CARD_TYPE)
                .credit(CREDIT_CARD_ID)
                .amount(MOVEMENT_CREDIT_CARD_AMOUNT)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(movementCreditCardRepository.findAll())
                .thenReturn(Flux.just(movementCreditCard));

        StepVerifier.create(movementCreditCardService.getAll())
                .expectNext(MovementResponse.fromModelMovementCreditCard(movementCreditCard))
                .verifyComplete();
    }

    @Test
    void getById() {
        MovementCreditCard movementCreditCard = MovementCreditCard.builder()
                .id(MOVEMENT_CREDIT_CARD_ID)
                .type(MOVEMENT_CREDIT_CARD_TYPE)
                .credit(CREDIT_CARD_ID)
                .amount(MOVEMENT_CREDIT_CARD_AMOUNT)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(movementCreditCardRepository.findById(MOVEMENT_CREDIT_CARD_ID))
                .thenReturn(Mono.just(movementCreditCard));

        StepVerifier.create(movementCreditCardService.getById(MOVEMENT_CREDIT_CARD_ID))
                .expectNext(MovementResponse.fromModelMovementCreditCard(movementCreditCard))
                .verifyComplete();
    }

    @Test
    void getAllMovementsByCredit() {
        MovementCreditCard movementCreditCard = MovementCreditCard.builder()
                .id(MOVEMENT_CREDIT_CARD_ID)
                .type(MOVEMENT_CREDIT_CARD_TYPE)
                .credit(CREDIT_CARD_ID)
                .amount(MOVEMENT_CREDIT_CARD_AMOUNT)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(movementCreditCardRepository.findByCredit(CREDIT_CARD_ID))
                .thenReturn(Flux.just(movementCreditCard));

        StepVerifier.create(movementCreditCardService.getAllMovementsByCredit(CREDIT_CARD_ID))
                .expectNext(MovementResponse.fromModelMovementCreditCard(movementCreditCard))
                .verifyComplete();
    }

    @Test
    void getAverageDailyBalance() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstOfMonth = currentMonth.atDay(1);
        LocalDate last = currentMonth.atEndOfMonth();

        CreditCard creditCard = CreditCard.builder()
                .id(CREDIT_CARD_ID)
                .number(CREDIT_CARD_NUMBER)
                .billingDay(CREDIT_CARD_BILLING_DAY)
                .client(CREDIT_CARD_CLIENT)
                .amount(CREDIT_CARD_AMOUNT)
                .paymentDay(CREDIT_CARD_PAYMENT_DAY)
                .tcea(CREDIT_CARD_TCEA)
                .balance(CREDIT_CARD_BALANCE)
                .build();

        when(creditCardRepository.findById(CREDIT_CARD_ID))
                .thenReturn(Mono.just(creditCard));

        MovementCreditCard movementCreditCard = MovementCreditCard.builder()
                .id(MOVEMENT_CREDIT_CARD_ID)
                .type(MOVEMENT_CREDIT_CARD_TYPE)
                .credit(CREDIT_CARD_ID)
                .amount(MOVEMENT_CREDIT_CARD_AMOUNT)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(movementCreditCardRepository.findByCreditAndDateBetween(CREDIT_CARD_ID, firstOfMonth, last))
                .thenReturn(Flux.just(movementCreditCard));

        StepVerifier.create(movementCreditCardService.getAverageDailyBalance(CREDIT_CARD_ID))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllReportByClient() {

        YearMonth currentMonth = YearMonth.now();
        LocalDate firstOfMonth = currentMonth.atDay(1);
        LocalDate last = currentMonth.atEndOfMonth();

        CreditCard creditCard = CreditCard.builder()
                .id(CREDIT_CARD_ID)
                .number(CREDIT_CARD_NUMBER)
                .billingDay(CREDIT_CARD_BILLING_DAY)
                .client(CREDIT_CARD_CLIENT)
                .amount(CREDIT_CARD_AMOUNT)
                .paymentDay(CREDIT_CARD_PAYMENT_DAY)
                .tcea(CREDIT_CARD_TCEA)
                .balance(CREDIT_CARD_BALANCE)
                .build();

        when(creditCardRepository.findById(CREDIT_CARD_ID))
                .thenReturn(Mono.just(creditCard));

        when(creditCardRepository.findCreditCardsByClient(CREDIT_CARD_CLIENT))
                .thenReturn(Flux.just(creditCard));

        MovementCreditCard movementCreditCard = MovementCreditCard.builder()
                .id(MOVEMENT_CREDIT_CARD_ID)
                .type(MOVEMENT_CREDIT_CARD_TYPE)
                .credit(CREDIT_CARD_ID)
                .amount(MOVEMENT_CREDIT_CARD_AMOUNT)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(movementCreditCardRepository.findByCreditAndDateBetween(CREDIT_CARD_ID, firstOfMonth, last))
                .thenReturn(Flux.just(movementCreditCard));

        StepVerifier.create(movementCreditCardService.getAllReportsByClient(CREDIT_CARD_CLIENT))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void delete() {
        MovementCreditCard movementCreditCard = MovementCreditCard.builder()
                .id(MOVEMENT_CREDIT_CARD_ID)
                .type(MOVEMENT_CREDIT_CARD_TYPE)
                .credit(CREDIT_CARD_ID)
                .amount(MOVEMENT_CREDIT_CARD_AMOUNT)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(movementCreditCardRepository.findById(MOVEMENT_CREDIT_CARD_ID))
                .thenReturn(Mono.just(movementCreditCard));

        when(movementCreditCardRepository.delete(movementCreditCard))
                .thenReturn(Mono.empty());

        StepVerifier.create(movementCreditCardService.delete(MOVEMENT_CREDIT_CARD_ID))
                .verifyComplete();
    }

    @Test
    void deleteAll() {
        when(movementCreditCardRepository.deleteAll())
                .thenReturn(Mono.empty());

        StepVerifier.create(movementCreditCardService.deleteAll())
                .verifyComplete();
    }
}

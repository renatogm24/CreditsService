package com.group7.creditsservice.serviceimpl;

import com.group7.creditsservice.dto.CreditCardRequest;
import com.group7.creditsservice.dto.CreditCardResponse;
import com.group7.creditsservice.dto.CreditReportResponse;
import com.group7.creditsservice.exception.credit.CreditCreationException;
import com.group7.creditsservice.exception.credit.CreditNotFoundException;
import com.group7.creditsservice.model.Client;
import com.group7.creditsservice.model.CreditCard;
import com.group7.creditsservice.repository.BillingCreditCardRepository;
import com.group7.creditsservice.repository.CreditCardRepository;
import com.group7.creditsservice.repository.MovementCreditCardRepository;
import com.group7.creditsservice.utils.WebClientUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

class CreditCardServiceImplTest {
    private static final String CREDIT_CARD_ID = "628dd0ef97545d60564b93f5";
    private static final String CREDIT_CARD_CLIENT = "62784dd906dc7f2ed86d12d5";
    private static final Double CREDIT_CARD_AMOUNT = 1000.0;
    private static final Double CREDIT_CARD_BALANCE = 0.0;
    private static final Integer CREDIT_CARD_PAYMENT_DAY = 20;
    private static final String CREDIT_CARD_NUMBER = "4213-1212-12122-1212";
    private static final Integer CREDIT_CARD_BILLING_DAY = 10;
    private static final Double CREDIT_CARD_TCEA = 10.5;

    private static final String CREDIT_CARD_CLIENT_NAME = "Aurora";
    private static final String CREDIT_CARD_CLIENT_TYPE = "Personal";
    private static final String CREDIT_CARD_CLIENT_DOCUMENT_TYPE = "DNI";
    private static final String CREDIT_CARD_CLIENT_PROFILE = "";
    private static final String CREDIT_CARD_CLIENT_DOCUMENT_NUMBER = "77128391";

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private BillingCreditCardRepository billingCreditCardRepository;

    @Mock
    private MovementCreditCardRepository movementCreditCardRepository;

    @InjectMocks
    private CreditCardServiceImpl creditCardService;

    @Mock
    private WebClientUtils webClientUtils;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateCreditCard() {
        CreditCardRequest creditCardRequest = CreditCardRequest.builder()
                .client(CREDIT_CARD_CLIENT)
                .amount(CREDIT_CARD_AMOUNT)
                .paymentDay(CREDIT_CARD_PAYMENT_DAY)
                .number(CREDIT_CARD_NUMBER)
                .billingDay(CREDIT_CARD_BILLING_DAY)
                .tcea(CREDIT_CARD_TCEA)
                .build();

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

        Client client = Client.builder()
                .id(CREDIT_CARD_CLIENT)
                .name(CREDIT_CARD_CLIENT_NAME)
                .type(CREDIT_CARD_CLIENT_TYPE)
                .profile(CREDIT_CARD_CLIENT_PROFILE)
                .documentType(CREDIT_CARD_CLIENT_DOCUMENT_TYPE)
                .documentNumber(CREDIT_CARD_CLIENT_DOCUMENT_NUMBER).build();

        when(webClientUtils.getClient(any()))
                .thenReturn(Mono.just(client));

        when(creditCardRepository.save(any()))
                .thenReturn(Mono.just(creditCard));

        StepVerifier.create(creditCardService.saveCreditCard(creditCardRequest))
                .assertNext(creditCardResponse -> {
                    assertNotNull(creditCardResponse);
                    assertEquals(CREDIT_CARD_ID, creditCardResponse.getId());
                    assertEquals(CREDIT_CARD_CLIENT, creditCardResponse.getClient());
                    assertEquals(CREDIT_CARD_AMOUNT, creditCardResponse.getAmount());
                    assertEquals(CREDIT_CARD_BALANCE, creditCardResponse.getBalance());
                    assertEquals(CREDIT_CARD_NUMBER, creditCardResponse.getNumber());
                    assertEquals(CREDIT_CARD_BILLING_DAY, creditCardResponse.getBillingDay());
                    assertEquals(CREDIT_CARD_TCEA, creditCardResponse.getTcea());
                })
                .verifyComplete();
    }

    @Test
    void findAllCreditCars() {
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

        when(creditCardRepository.findAll())
                .thenReturn(Flux.just(creditCard));

        StepVerifier.create(creditCardService.findAllCreditCars())
                .expectNext(CreditCardResponse.fromModel(creditCard))
                .verifyComplete();
    }

    @Test
    void getAllCreditCardsByClient() {
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

        when(creditCardRepository.findCreditCardsByClient(CREDIT_CARD_CLIENT))
                .thenReturn(Flux.just(creditCard));

        StepVerifier.create(creditCardService.getAllCreditCardsByClient(CREDIT_CARD_CLIENT))
                .expectNext(CreditCardResponse.fromModel(creditCard))
                .verifyComplete();
    }

    @Test
    void getById() {
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

        when(creditCardRepository.findById(CREDIT_CARD_CLIENT))
                .thenReturn(Mono.just(creditCard));

        StepVerifier.create(creditCardService.getById(CREDIT_CARD_CLIENT))
                .expectNext(CreditCardResponse.fromModel(creditCard))
                .verifyComplete();
    }

    @Test
    void getReport() {
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

        CreditReportResponse response = CreditReportResponse.fromModel(creditCard);
        response.setBillings(Collections.emptyList());
        response.setMovements(Collections.emptyList());

        LocalDate from = LocalDate.of(2022, 3, 30);
        LocalDate to = LocalDate.of(2022, 5, 25);

        when(creditCardRepository.findById(CREDIT_CARD_ID))
                .thenReturn(Mono.just(creditCard));

        when(billingCreditCardRepository.findByCreditAndBillingDateBetween(CREDIT_CARD_ID, from, to))
                .thenReturn(Flux.empty());

        when(movementCreditCardRepository.findByCreditAndDateBetween(CREDIT_CARD_ID, from, to))
                .thenReturn(Flux.empty());

        StepVerifier.create(creditCardService.getReport(CREDIT_CARD_ID, from, to))
                .expectNext(response)
                .verifyComplete();

    }

    @Test
    void getReportNotFound() {
        when(creditCardRepository.findById(CREDIT_CARD_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(creditCardService.getReport(CREDIT_CARD_ID,
                LocalDate.of(2022, 03, 30),
                LocalDate.of(2022, 05, 25)))
                .verifyError(CreditNotFoundException.class);
    }

    @Test
    void updateCreditCard() {
        CreditCardRequest creditCardRequest = CreditCardRequest.builder()
                .client(CREDIT_CARD_CLIENT)
                .amount(CREDIT_CARD_AMOUNT)
                .paymentDay(CREDIT_CARD_PAYMENT_DAY)
                .number(CREDIT_CARD_NUMBER)
                .billingDay(CREDIT_CARD_BILLING_DAY)
                .tcea(CREDIT_CARD_TCEA)
                .build();

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

        when(creditCardRepository.save(creditCard))
                .thenReturn(Mono.just(creditCard));

        StepVerifier.create(creditCardService.updateCreditCard(CREDIT_CARD_ID, creditCardRequest))
                .expectNext(CreditCardResponse.fromModel(creditCard))
                .verifyComplete();
    }

    @Test
    void updateCreditCardNotFound() {
        CreditCardRequest creditCardRequest = CreditCardRequest.builder()
                .client(CREDIT_CARD_CLIENT)
                .amount(CREDIT_CARD_AMOUNT)
                .paymentDay(CREDIT_CARD_PAYMENT_DAY)
                .number(CREDIT_CARD_NUMBER)
                .billingDay(CREDIT_CARD_BILLING_DAY)
                .tcea(CREDIT_CARD_TCEA)
                .build();

        when(creditCardRepository.findById(CREDIT_CARD_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(creditCardService.updateCreditCard(CREDIT_CARD_ID, creditCardRequest))
                .verifyError(CreditNotFoundException.class);
    }

    @Test
    void deleteCreditCard() {
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

        when(creditCardRepository.delete(creditCard))
                .thenReturn(Mono.empty());

        StepVerifier.create(creditCardService.deleteCreditCard(CREDIT_CARD_ID))
                .verifyComplete();
    }

    @Test
    void deleteCreditCardNotFound() {
        when(creditCardRepository.findById(CREDIT_CARD_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(creditCardService.deleteCreditCard(CREDIT_CARD_ID))
                .verifyError(CreditNotFoundException.class);
    }
}

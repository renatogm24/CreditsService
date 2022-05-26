package com.group7.creditsservice.serviceimpl;

import com.group7.creditsservice.dto.LoanRequest;
import com.group7.creditsservice.dto.LoanResponse;
import com.group7.creditsservice.exception.credit.CreditNotFoundException;
import com.group7.creditsservice.model.Loan;
import com.group7.creditsservice.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoanServiceImplTest {
    private static final String LOAN_ID = "628ecef4ae24b20818af59f7";
    private static final String LOAN_CLIENT = "62784dd906dc7f2ed86d12d5";
    private static final Double LOAN_AMOUNT = 1000.0;
    private static final Double LOAN_BALANCE = 0.0;
    private static final int LOAN_PAYMENT_DAY = 20;
    private static final Double LOAN_FULL_PAYMENT = 1860.0;
    private static final Integer LOAN_NUMBER_INSTALLMENTS = 36;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllLoans() {
        Loan loan = Loan.builder()
                .id(LOAN_ID)
                .client(LOAN_CLIENT)
                .amount(LOAN_AMOUNT)
                .balance(LOAN_BALANCE)
                .paymentDay(LOAN_PAYMENT_DAY)
                .fullPayment(LOAN_FULL_PAYMENT)
                .numberInstallments(LOAN_NUMBER_INSTALLMENTS)
                .build();

        when(loanRepository.findAll())
                .thenReturn(Flux.just(loan));

        StepVerifier.create(loanService.findAllLoans())
                .expectNext(LoanResponse.fromModel(loan))
                .verifyComplete();

    }

    @Test
    void getAllLoansByClient() {
        Loan loan = Loan.builder()
                .id(LOAN_ID)
                .client(LOAN_CLIENT)
                .amount(LOAN_AMOUNT)
                .balance(LOAN_BALANCE)
                .paymentDay(LOAN_PAYMENT_DAY)
                .fullPayment(LOAN_FULL_PAYMENT)
                .numberInstallments(LOAN_NUMBER_INSTALLMENTS)
                .build();

        when(loanRepository.findLoansByClient(LOAN_CLIENT))
                .thenReturn(Flux.just(loan));

        StepVerifier.create(loanService.getAllLoansByClient(LOAN_CLIENT))
                .expectNext(LoanResponse.fromModel(loan))
                .verifyComplete();

    }

    @Test
    void saveLoan() {
        Loan loan = Loan.builder()
                .id(LOAN_ID)
                .client(LOAN_CLIENT)
                .amount(LOAN_AMOUNT)
                .balance(LOAN_BALANCE)
                .paymentDay(LOAN_PAYMENT_DAY)
                .fullPayment(LOAN_FULL_PAYMENT)
                .numberInstallments(LOAN_NUMBER_INSTALLMENTS)
                .build();

        LoanRequest loanRequest = LoanRequest.builder()
                    .client(LOAN_CLIENT)
                    .amount(LOAN_AMOUNT)
                    .paymentDay(LOAN_PAYMENT_DAY)
                    .fullPayments(LOAN_FULL_PAYMENT)
                    .numberInstallments(LOAN_NUMBER_INSTALLMENTS)
                    .build();

        when(loanRepository.insert(loan))
                .thenReturn(Mono.just(loan));

        StepVerifier.create(loanService.saveLoan(Mono.just(loanRequest)))
                .expectNext(LoanResponse.fromModel(loan))
                .verifyComplete();
    }

    @Test
    void update() {
        LoanRequest loanRequest = LoanRequest.builder()
                .client(LOAN_CLIENT)
                .amount(LOAN_AMOUNT)
                .paymentDay(LOAN_PAYMENT_DAY)
                .fullPayments(LOAN_FULL_PAYMENT)
                .numberInstallments(LOAN_NUMBER_INSTALLMENTS)
                .build();

        Loan loan = Loan.builder()
                .id(LOAN_ID)
                .client(LOAN_CLIENT)
                .amount(LOAN_AMOUNT)
                .balance(LOAN_BALANCE)
                .paymentDay(LOAN_PAYMENT_DAY)
                .fullPayment(LOAN_FULL_PAYMENT)
                .numberInstallments(LOAN_NUMBER_INSTALLMENTS)
                .build();

        when(loanRepository.findById(LOAN_ID))
                .thenReturn(Mono.just(loan));

        when(loanRepository.save(loan))
                .thenReturn(Mono.just(loan));

        StepVerifier.create(loanService.update(LOAN_ID, loanRequest))
                .expectNext(LoanResponse.fromModel(loan))
                .verifyComplete();
    }

    @Test
    void updateNotFound() {
        LoanRequest loanRequest = LoanRequest.builder()
                .client(LOAN_CLIENT)
                .amount(LOAN_AMOUNT)
                .paymentDay(LOAN_PAYMENT_DAY)
                .fullPayments(LOAN_FULL_PAYMENT)
                .numberInstallments(LOAN_NUMBER_INSTALLMENTS)
                .build();

        when(loanRepository.findById(LOAN_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(loanService.update(LOAN_ID, loanRequest))
                .verifyError(CreditNotFoundException.class);
    }

    @Test
    void delete() {
        Loan loan = Loan.builder()
                .id(LOAN_ID)
                .client(LOAN_CLIENT)
                .amount(LOAN_AMOUNT)
                .balance(LOAN_BALANCE)
                .paymentDay(LOAN_PAYMENT_DAY)
                .fullPayment(LOAN_FULL_PAYMENT)
                .numberInstallments(LOAN_NUMBER_INSTALLMENTS)
                .build();

        when(loanRepository.findById(LOAN_ID))
                .thenReturn(Mono.just(loan));

        when(loanRepository.delete(loan))
                .thenReturn(Mono.empty());

        StepVerifier.create(loanService.delete(LOAN_ID))
                .verifyComplete();
    }

    @Test
    void deleteNotFound() {
        when(loanRepository.findById(LOAN_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(loanService.delete(LOAN_ID))
                .verifyError(CreditNotFoundException.class);
    }

}

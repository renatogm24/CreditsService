package com.group7.creditsservice.serviceimpl;

import com.group7.creditsservice.dto.LoanRequest;
import com.group7.creditsservice.dto.MovementRequest;
import com.group7.creditsservice.dto.MovementResponse;
import com.group7.creditsservice.exception.movement.MovementCreationException;
import com.group7.creditsservice.exception.movement.MovementNotFoundException;
import com.group7.creditsservice.model.Loan;
import com.group7.creditsservice.model.MovementLoan;
import com.group7.creditsservice.repository.LoanRepository;
import com.group7.creditsservice.repository.MovementLoanRepository;
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

import static org.mockito.Mockito.when;

class MovementLoanServiceImplTest {

    private static final String MOVEMENT_LOAN_ID = "628c37e8ab7e7a535f813a3c";
    private static final String LOAN_ID = "6287fd9ea2b9a70d21a2c8a5";
    private static final String MOVEMENT_LOAN_TYPE = "deposit";
    private static final Double MOVEMENT_AMOUNT = 1000.0;

    private static final String LOAN_CLIENT = "62784dd906dc7f2ed86d12d5";
    private static final Double LOAN_AMOUNT = 1000.0;
    private static final Double LOAN_BALANCE = 0.0;
    private static final int LOAN_PAYMENT_DAY = 20;
    private static final Double LOAN_FULL_PAYMENT = 1860.0;
    private static final Integer LOAN_NUMBER_INSTALLMENTS = 36;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private MovementLoanRepository movementLoanRepository;

    @InjectMocks
    private MovementLoanServiceImpl movementLoanService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll() {
        MovementLoan movementLoan = MovementLoan.builder()
                .id(MOVEMENT_LOAN_ID)
                .type(MOVEMENT_LOAN_TYPE)
                .loan(LOAN_ID)
                .amount(MOVEMENT_AMOUNT)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(movementLoanRepository.findAll())
                .thenReturn(Flux.just(movementLoan));

        StepVerifier.create(movementLoanService.getAll())
                .expectNext(MovementResponse.fromModelMovementLoan(movementLoan))
                .verifyComplete();
    }

    @Test
    void getById() {
        MovementLoan movementLoan = MovementLoan.builder()
                .id(MOVEMENT_LOAN_ID)
                .type(MOVEMENT_LOAN_TYPE)
                .loan(LOAN_ID)
                .amount(MOVEMENT_AMOUNT)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(movementLoanRepository.findById(MOVEMENT_LOAN_ID))
                .thenReturn(Mono.just(movementLoan));

        StepVerifier.create(movementLoanService.getById(MOVEMENT_LOAN_ID))
                .expectNext(MovementResponse.fromModelMovementLoan(movementLoan))
                .verifyComplete();
    }

    @Test
    void getByIdNotFound() {
        when(movementLoanRepository.findById(MOVEMENT_LOAN_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(movementLoanService.getById(MOVEMENT_LOAN_ID))
                .verifyError(MovementNotFoundException.class);
    }

    @Test
    void getAllMovementsByLoan() {
        MovementLoan movementLoan = MovementLoan.builder()
                .id(MOVEMENT_LOAN_ID)
                .type(MOVEMENT_LOAN_TYPE)
                .loan(LOAN_ID)
                .amount(MOVEMENT_AMOUNT)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(movementLoanRepository.findByLoan(LOAN_ID))
                .thenReturn(Flux.just(movementLoan));

        StepVerifier.create(movementLoanService.getAllMovementsByLoan(LOAN_ID))
                .expectNext(MovementResponse.fromModelMovementLoan(movementLoan))
                .verifyComplete();
    }

    @Test
    void getStateByLoanPerMonthAndYear() {
        int year = 2022;
        int month = 5;
        YearMonth currentMonth = YearMonth.of(year, month);
        LocalDate firstOfMonth = currentMonth.atDay(1);
        LocalDate last = currentMonth.atEndOfMonth();

        MovementLoan movementLoan = MovementLoan.builder()
                .id(MOVEMENT_LOAN_ID)
                .type(MOVEMENT_LOAN_TYPE)
                .loan(LOAN_ID)
                .amount(MOVEMENT_AMOUNT)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(movementLoanRepository.findByLoanAndDateBetween(LOAN_ID, firstOfMonth, last))
                .thenReturn(Flux.just(movementLoan));

        StepVerifier.create(movementLoanService.getStateByLoanPerMonthAndYear(LOAN_ID, year, month))
                .expectNext(1000.0)
                .verifyComplete();
    }

    @Test
    void delete() {
        MovementLoan movementLoan = MovementLoan.builder()
                .id(MOVEMENT_LOAN_ID)
                .type(MOVEMENT_LOAN_TYPE)
                .loan(LOAN_ID)
                .amount(MOVEMENT_AMOUNT)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(movementLoanRepository.findById(MOVEMENT_LOAN_ID))
                .thenReturn(Mono.just(movementLoan));

        when(movementLoanRepository.delete(movementLoan))
                .thenReturn(Mono.empty());

        StepVerifier.create(movementLoanService.delete(MOVEMENT_LOAN_ID))
                .verifyComplete();
    }

    @Test
    void deleteAll() {
        when(movementLoanService.deleteAll())
                .thenReturn(Mono.empty());

        StepVerifier.create(movementLoanService.deleteAll())
                .verifyComplete();
    }

    @Test
    void save() {
        MovementRequest movementRequest = MovementRequest.builder()
                .type(MOVEMENT_LOAN_TYPE)
                .credit(LOAN_ID)
                .amount(MOVEMENT_AMOUNT)
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

        MovementLoan movementLoan = MovementLoan.builder()
                .id(MOVEMENT_LOAN_ID)
                .type(MOVEMENT_LOAN_TYPE)
                .loan(LOAN_ID)
                .amount(MOVEMENT_AMOUNT)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(loanRepository.findById(LOAN_ID))
                .thenReturn(Mono.just(loan));

        when(loanRepository.save(loan))
                .thenReturn(Mono.just(loan));

        when(movementLoanRepository.insert(movementLoan))
                .thenReturn(Mono.just(movementLoan));

        StepVerifier.create(movementLoanService.save(movementRequest))
                .expectNext(MovementResponse.fromModelMovementLoan(movementLoan))
                .verifyComplete();
    }

    @Test
    void saveLimitMovements() {
        MovementRequest movementRequest = MovementRequest.builder()
                .type("withdraw")
                .credit(LOAN_ID)
                .amount(2000.0)
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

        MovementLoan movementLoan = MovementLoan.builder()
                .id(MOVEMENT_LOAN_ID)
                .type(MOVEMENT_LOAN_TYPE)
                .loan(LOAN_ID)
                .amount(MOVEMENT_AMOUNT)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(loanRepository.findById(LOAN_ID))
                .thenReturn(Mono.just(loan));

        when(loanRepository.save(loan))
                .thenReturn(Mono.just(loan));

        when(movementLoanRepository.insert(movementLoan))
                .thenReturn(Mono.just(movementLoan));

        StepVerifier.create(movementLoanService.save(movementRequest))
                .verifyError(MovementCreationException.class);
    }
}

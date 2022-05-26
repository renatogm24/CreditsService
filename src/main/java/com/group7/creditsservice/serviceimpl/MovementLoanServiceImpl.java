package com.group7.creditsservice.serviceimpl;

import com.group7.creditsservice.dto.MovementRequest;
import com.group7.creditsservice.dto.MovementResponse;
import com.group7.creditsservice.exception.movement.MovementNotFoundException;
import com.group7.creditsservice.exception.movement.MovementCreationException;
import com.group7.creditsservice.repository.LoanRepository;
import com.group7.creditsservice.repository.MovementLoanRepository;
import com.group7.creditsservice.service.MovementLoanService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
@AllArgsConstructor
@Slf4j

public class MovementLoanServiceImpl implements MovementLoanService {

    private LoanRepository loanRepository;
    private MovementLoanRepository movementLoanRepository;

    @Override
    public Flux<MovementResponse> getAll() {
        return movementLoanRepository.findAll().map(MovementResponse::fromModelMovementLoan);
    }

    @Override
    public Mono<MovementResponse> getById(String id) {
        return movementLoanRepository.findById(id)
                .switchIfEmpty(Mono.error(new MovementNotFoundException("Movement not found with id:"+id)))
                .map(MovementResponse::fromModelMovementLoan);
    }

    @Override
    public Flux<MovementResponse> getAllMovementsByLoan(String loan) {
        return movementLoanRepository.findByLoan(loan).map(MovementResponse::fromModelMovementLoan);
    }

    @Override
    public Mono<Double> getStateByLoanPerMonthAndYear(String loan, int year, int month) {
        YearMonth currentMonth = YearMonth.of(year, month);
        LocalDate firstOfMonth = currentMonth.atDay(1);
        LocalDate last = currentMonth.atEndOfMonth();

        return movementLoanRepository.findByLoanAndDateBetween(loan,firstOfMonth, last)
                .switchIfEmpty(Flux.empty())
                .reduce(0.0, (x, y)->x+y.getAmountSigned());
    }

    @Override
    public Mono<Void> delete(String id) {
        return movementLoanRepository.findById(id)
                .switchIfEmpty(Mono.error(new MovementCreationException("Movement not found with id:"+id)))
                .flatMap(existingLoan ->
                        movementLoanRepository.delete(existingLoan)
                );
    }

    @Override
    public Mono<Void> deleteAll() {
        return movementLoanRepository.deleteAll();
    }

    @Override
    public Mono<MovementResponse> save(MovementRequest movementRequest) {
        return Mono.just(movementRequest)
                .map(MovementRequest::toModelMovementLoan)
                .flatMap(movement -> loanRepository.findById(movement.getLoan())
                        .switchIfEmpty(Mono.error(new MovementCreationException("Loan not found with id:"+movement.getLoan())))
                        .flatMap(existingLoan -> {
                            if (!existingLoan.isMovementValid(movement.getType(), movement.getAmount()))
                                return Mono.error(new MovementCreationException("You reach the limit of your credit card"));
                            existingLoan.makeMovement(movement.getType(), movement.getAmount());
                            return loanRepository.save(existingLoan)
                                    .then(movementLoanRepository.insert(movement));
                        }))
                .map(MovementResponse::fromModelMovementLoan)
                .onErrorMap(ex -> new MovementCreationException(ex.getMessage()));
    }
}

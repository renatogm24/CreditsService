package com.group7.creditsservice.service;

import com.group7.creditsservice.model.Credit;
import com.group7.creditsservice.model.CreditCard;
import com.group7.creditsservice.model.MovementCreditCard;
import com.group7.creditsservice.exception.movement.MovementCreationException;
import com.group7.creditsservice.repository.CreditCardRepository;
import com.group7.creditsservice.repository.MovementCreditCardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class MovementCreditCardServiceImpl implements MovementCreditCardService {
    private MovementCreditCardRepository movementRepository;

    private CreditCardRepository creditCardRepository;

    @Override
    public Flux<MovementCreditCard> getAll() {
        return movementRepository.findAll();
    }

    @Override
    public Mono<MovementCreditCard> getById(String id) {
        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(new MovementCreationException("Movement not found with id: " + id)));
    }

    @Override
    public Flux<MovementCreditCard> getAllMovementsByCredit(String credit) {
        return movementRepository.findByCredit(credit);
    }

    @Override
    public Mono<Double> getStateByCreditPerMonthAndYear(String credit, int year, int month) {

        YearMonth currentMonth = YearMonth.of(year, month);
        LocalDate firstOfMonth = currentMonth.atDay(1);
        LocalDate last = currentMonth.atEndOfMonth();

        return movementRepository.findByCreditAndDateBetween(credit, firstOfMonth, last)
                .doOnEach(System.out::println)
                .reduce(0.0,(x,y)-> x + y.getAmountSigned());

    }

    private Flux<MovementCreditCard> getMovementsOfCurrentMonthByCreditCard(String credit) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstOfMonth = currentMonth.atDay(1);
        LocalDate last = currentMonth.atEndOfMonth();

        return movementRepository.findByCreditAndDateBetween(credit, firstOfMonth, last);
    }

    @Override
    public Mono<Double> getAverageDailyBalance(String credit) {
        int numDays = LocalDate.now().getDayOfMonth();

        Mono<Double> lastBalance = creditCardRepository.findById(credit)
                .map(CreditCard::getBalance);

        Mono<Double> sumOfMonthMovements = getMovementsOfCurrentMonthByCreditCard(credit)
                .reduce(0.0, (x1, x2) -> x1 + x2.getAmountSigned());

        Mono<Double> sumOfAverageDailyMovements = getMovementsOfCurrentMonthByCreditCard(credit)
                .map(movement -> movement.getAmountSigned() * (numDays - movement.getDayOfMovement() + 1))
                .reduce(0.0, Double::sum);

        return Mono.zip(lastBalance, sumOfMonthMovements, sumOfAverageDailyMovements)
                .map(result -> {
                    Double lastBalanceResult = result.getT1();
                    Double sumOfMonthMovementResult = result.getT2();
                    Double sumOfAverageDailyMovementResult = result.getT3();
                    Double initBalance = (lastBalanceResult - sumOfMonthMovementResult) * numDays;
                    return (initBalance + sumOfAverageDailyMovementResult) / numDays;
                });
    }

    @Override
    public Flux<Map<String, Double>> getAllReportsByClient(String client) {
        return creditCardRepository.findCreditCardsByClient(client)
                .flatMap(creditCard -> getAverageDailyBalance(creditCard.getId())
                        .map(result -> Collections.singletonMap(creditCard.getId(), result)));
    }


    @Override
    public Mono<MovementCreditCard> save(MovementCreditCard movementRequest) {
        return Mono.just(movementRequest)
                .flatMap(movement -> creditCardRepository.findById(movement.getCredit())
                        .switchIfEmpty(Mono.error(new MovementCreationException("Account not found with id: " + movement.getCredit())))
                        .flatMap(existingCredit -> {
                            if (!existingCredit.isMovementValid(movement.getType(), movement.getAmount()))
                                return Mono.error(new MovementCreationException("You reach the limit of your credit card"));
                            existingCredit.makeMovement(movement.getType(), movement.getAmount());
                            return creditCardRepository.save(existingCredit);
                        }))
                .then(Mono.just(movementRequest))
                .map(movement -> {
                    LocalDateTime lt = LocalDateTime.now();
                    movement.setDate(lt);
                    return movement;
                })
                .flatMap(movement -> movementRepository.insert(movement))
                .onErrorMap(ex -> new MovementCreationException(ex.getMessage()));
    }

    @Override
    public Mono<MovementCreditCard> update(String id, MovementCreditCard movementRequest) {

        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(new MovementCreationException("Credit not found with id: " + id)))
                .doOnError(ex -> log.error("Account not found with id: {}", id, ex))
                .flatMap(existingAccount -> {
                    existingAccount.setDate(movementRequest.getDate());
                    return movementRepository.save(existingAccount);
                })
                .doOnSuccess(res -> log.info("Updated movement with ID: {}", res.getId()));

    }

    @Override
    public Mono<Void> delete(String id) {
        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(new MovementCreationException("Movement not found with id: " + id)))
                .flatMap(existingAccount ->
                        movementRepository.delete(existingAccount)
                );
    }

    @Override
    public Mono<Void> deleteAll() {
        return movementRepository.deleteAll();
    }

}

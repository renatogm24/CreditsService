package com.group7.creditsservice.serviceimpl;

import com.group7.creditsservice.dto.MovementRequest;
import com.group7.creditsservice.dto.MovementResponse;
import com.group7.creditsservice.model.CreditCard;
import com.group7.creditsservice.model.MovementCreditCard;
import com.group7.creditsservice.exception.movement.MovementCreationException;
import com.group7.creditsservice.repository.BillingCreditCardRepository;
import com.group7.creditsservice.repository.CreditCardRepository;
import com.group7.creditsservice.repository.MovementCreditCardRepository;
import com.group7.creditsservice.service.MovementCreditCardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class MovementCreditCardServiceImpl implements MovementCreditCardService {
    private MovementCreditCardRepository movementRepository;

    private CreditCardRepository creditCardRepository;

    private BillingCreditCardRepository billingCreditCardRepository;

    @Override
    public Flux<MovementResponse> getAll() {
        return movementRepository.findAll().map(MovementResponse::fromModelMovementCreditCard);
    }

    @Override
    public Mono<MovementResponse> getById(String id) {
        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(new MovementCreationException("Movement not found with id: " + id)))
                .map(MovementResponse::fromModelMovementCreditCard);
    }

    @Override
    public Flux<MovementResponse> getAllMovementsByCredit(String credit) {
        return movementRepository.findByCredit(credit).map(MovementResponse::fromModelMovementCreditCard);
    }

    @Override
    public Flux<MovementResponse> getLatestMovementsByCredit(String credit) {
        return movementRepository.findByCreditOrderByCreatedAtDesc(credit).map(MovementResponse::fromModelMovementCreditCard).take(10);
    }

    @Override
    public Mono<Double> getStateByCreditPerMonthAndYear(String credit, int year, int month) {

        YearMonth currentMonth = YearMonth.of(year, month);
        LocalDate firstOfMonth = currentMonth.atDay(1);
        LocalDate last = currentMonth.atEndOfMonth();

        return movementRepository.findByCreditAndDateBetween(credit, firstOfMonth, last)
                .reduce(0.0, (x, y) -> x + y.getAmountSigned());

    }

    private Flux<MovementCreditCard> getMovementsOfCurrentMonthByCreditCard(String credit) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstOfMonth = currentMonth.atDay(1);
        LocalDate last = currentMonth.atEndOfMonth();

        return movementRepository.findByCreditAndDateBetween(credit, firstOfMonth, last);
    }

    @Override
    public Mono<Double> getAverageDailyBalance(final String credit) {
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
    public Mono<MovementResponse> save(MovementCreditCard movementRequest) {
        return Mono.just(movementRequest)
                .flatMap(movement -> creditCardRepository.findById(movement.getCredit())
                        .switchIfEmpty(Mono.error(new MovementCreationException("Account not found with id: " + movement.getCredit())))
                        .flatMap(existingCredit -> {
                            if (!existingCredit.isMovementValid(movement.getType(), movement.getAmount()))
                                return Mono.error(new MovementCreationException("You reach the limit of your credit card"));
                            existingCredit.makeMovement(movement.getType(), movement.getAmount());

                            return creditCardRepository.save(existingCredit)
                                    .then(movementRepository.insert(movement));

                        })
                        .flatMap(movement2 -> billingCreditCardRepository.findByCreditAndActiveIsTrue(movement2.getCredit())
                                .flatMap(billingCreditCard -> {
                                    if(movement2.getType().equalsIgnoreCase("deposit")) {
                                        billingCreditCard.updatePayment(movement2.getAmount());
                                        return billingCreditCardRepository.save(billingCreditCard);
                                    }

                                    return Mono.just(movement2);
                                })
                                .then(movementRepository.findById(movement.getId())))

                )
                .map(MovementResponse::fromModelMovementCreditCard)
                .onErrorMap(ex -> new MovementCreationException(ex.getMessage()))
                .doOnError(ex -> log.error("Error save", ex));
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

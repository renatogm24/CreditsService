package com.group7.creditsservice.repository;
import com.group7.creditsservice.model.MovementCreditCard;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface MovementCreditCardRepository extends ReactiveMongoRepository<MovementCreditCard, String> {

    Flux<MovementCreditCard> findByCredit(String account);

    Flux<MovementCreditCard> findByCreditOrderByCreatedAtDesc(String account);

    Flux<MovementCreditCard> findByCreditAndDateBetween(String credit, LocalDate from, LocalDate to);

}

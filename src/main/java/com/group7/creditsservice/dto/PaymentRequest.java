package com.group7.creditsservice.dto;

import com.group7.creditsservice.exception.movement.MovementCreationException;
import com.group7.creditsservice.model.MovementCreditCard;
import com.group7.creditsservice.model.MovementLoan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    @NotBlank
    private String credit;

    @NotNull
    @Positive
    private Double amount;

    @NotBlank
    private String clientPaying;

    public MovementCreditCard toModelMovementCreditCard() {

        return MovementCreditCard.builder()
                .type("deposit")
                .credit(this.credit)
                .amount(this.amount)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .clientPaying(this.clientPaying)
                .build();

    }

    public MovementLoan toModelMovementLoan() {

        return MovementLoan.builder()
                .type("deposit")
                .loan(this.credit)
                .amount(this.amount)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .clientPaying(this.clientPaying)
                .build();
    }
}

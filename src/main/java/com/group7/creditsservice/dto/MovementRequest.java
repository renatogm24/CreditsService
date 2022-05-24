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

public class MovementRequest {
    @NotBlank
    //@Pattern(regexp = "^(deposit|withdraw)$", message = "The type of movement must have a value from: 'Withdraw' or 'Deposit'")
    private String type;
    @NotBlank
    private String credit;

    @NotNull
    @Positive
    private Double amount;

    public MovementCreditCard toModelMovementCreditCard() {

        if (!type.equalsIgnoreCase("withdraw") && !type.equalsIgnoreCase("deposit"))
            throw new MovementCreationException("The type of movement must have a value from: 'withdraw' or 'deposit'");

        return MovementCreditCard.builder()
                .type(this.type)
                .credit(this.credit)
                .amount(this.amount)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

    }

    public MovementLoan toModelMovementLoan() {

        if (!type.equalsIgnoreCase("deposit"))
            throw new MovementCreationException("The type of movement must have a value from: 'deposit'");

        return MovementLoan.builder()
                .type(this.type)
                .loan(this.credit)
                .amount(this.amount)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();
    }
}

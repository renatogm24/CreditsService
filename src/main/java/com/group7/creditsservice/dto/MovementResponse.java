package com.group7.creditsservice.dto;

import com.group7.creditsservice.model.MovementCreditCard;
import com.group7.creditsservice.model.MovementLoan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class MovementResponse {
    private String id;
    private String type;
    private Double amount;
    private LocalDate date;
    private LocalDateTime createdAt;
    private String credit;
    private String clientPaying;

    public static MovementResponse fromModelMovementCreditCard(MovementCreditCard movementCreditCard) {
        return MovementResponse.builder()
                .id(movementCreditCard.getId())
                .type(movementCreditCard.getType())
                .amount(movementCreditCard.getAmount())
                .date(movementCreditCard.getDate())
                .createdAt(movementCreditCard.getCreatedAt())
                .credit(movementCreditCard.getCredit())
                .clientPaying(movementCreditCard.getClientPaying())
                .build();
    }

    public static MovementResponse fromModelMovementLoan(MovementLoan movementLoan) {
        return MovementResponse.builder()
                .id(movementLoan.getId())
                .type(movementLoan.getType())
                .amount(movementLoan.getAmount())
                .date(movementLoan.getDate())
                .createdAt(movementLoan.getCreatedAt())
                .credit(movementLoan.getLoan())
                .clientPaying(movementLoan.getClientPaying())
                .build();
    }
}

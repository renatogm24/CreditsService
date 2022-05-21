package com.group7.creditsservice.dto;

import com.group7.creditsservice.model.CreditCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CreditCardRequest {
    private String id;

    @NotBlank
    private String client;

    @NotBlank
    private String number;

    @NotNull
    @Min(value = 1)
    @Max(value = 25)
    private int paymentDay;

    @NotNull
    @Min(value = 1)
    @Max(value = 25)
    private int billingDay;

    @Positive
    @NotNull
    private double amount;

    @Positive
    @NotNull
    private double balance;

    public CreditCard toModel() {
        return CreditCard.builder()
                .client(this.client)
                .number(this.number)
                .paymentDay(this.paymentDay)
                .billingDay(this.billingDay)
                .amount(this.amount)
                .balance(this.balance)
                .build();
    }
}

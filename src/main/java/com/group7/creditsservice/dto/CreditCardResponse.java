package com.group7.creditsservice.dto;

import com.group7.creditsservice.model.CreditCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CreditCardResponse {
    private String id;
    private String client;
    private double amount;
    private double balance;
    private String number;
    private int billingDay;
    private double tcea;

    public static CreditCardResponse fromModel(CreditCard creditCard) {
        CreditCardResponseBuilder response = CreditCardResponse.builder()
                .id(creditCard.getId())
                .client(creditCard.getClient())
                .amount(creditCard.getAmount())
                .balance(creditCard.getBalance())
                .number(creditCard.getNumber())
                .billingDay(creditCard.getBillingDay())
                .tcea(creditCard.getTcea());

        return response.build();
    }
}

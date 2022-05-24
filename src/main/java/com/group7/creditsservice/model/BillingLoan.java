package com.group7.creditsservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "loanBillings")
public class BillingLoan extends Billing {
    private double payment;
    private int installment_number;

    public void updatePayment(final Double payment) {
        this.payment -= payment;
    }
}

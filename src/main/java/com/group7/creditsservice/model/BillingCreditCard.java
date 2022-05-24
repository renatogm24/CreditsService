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
@Document(collection = "creditCardBillings")
public class BillingCreditCard extends Billing {
    private double minPayment;
    private double fullPayment;

    public void updatePayment(final Double amount) {
        minPayment -= amount;
        fullPayment -= amount;
    }
}

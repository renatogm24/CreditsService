package com.group7.creditsservice.dto;

import com.group7.creditsservice.model.BillingCreditCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillingRequest {
    @NotBlank
    private String credit;

    @NotNull
    private LocalDate lastDayPayment;
    @NotNull
    private LocalDate billingDate;
    @NotNull
    private double minPayment;
    @NotNull
    private double fullPayment;

    public BillingCreditCard toModelBillingCreditCard() {
       return BillingCreditCard.builder()
               .credit(this.credit)
               .lastDayPayment(this.lastDayPayment)
               .billingDate(this.billingDate)
               .minPayment(this.minPayment)
               .fullPayment(this.fullPayment)
               .active(true)
               .build();

    }
}

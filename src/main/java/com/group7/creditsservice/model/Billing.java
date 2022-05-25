package com.group7.creditsservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Billing {
    @Id
    private String id;

    private String credit;
    private String client;
    private LocalDate lastDayPayment;
    private LocalDate billingDate;
    private boolean active;
}

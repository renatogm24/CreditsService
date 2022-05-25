package com.group7.creditsservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Client {
    private String id;
    private String name;
    private String type;
    private String profile;
    private String documentType;
    private String documentNumber;
}

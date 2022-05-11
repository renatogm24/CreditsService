package com.group7.creditsservice.controller;

import com.group7.creditsservice.service.ILoanService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/credits/loans/movement")
@AllArgsConstructor
@Slf4j

public class MovementLoanController {

    private ILoanService service;
}

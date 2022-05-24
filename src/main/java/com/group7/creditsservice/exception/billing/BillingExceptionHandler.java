package com.group7.creditsservice.exception.billing;

import com.group7.creditsservice.exception.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BillingExceptionHandler {
    @ExceptionHandler(BillingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleBillingNotFoundException(BillingNotFoundException ex) {
        return ExceptionResponse.builder().message(ex.getMessage()).build();
    }

    @ExceptionHandler(BillingCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleBillingCreationException(BillingCreationException ex) {
        return ExceptionResponse.builder().message(ex.getMessage()).build();
    }
}

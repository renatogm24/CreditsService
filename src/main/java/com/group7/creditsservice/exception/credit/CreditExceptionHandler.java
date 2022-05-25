package com.group7.creditsservice.exception.credit;

import com.group7.creditsservice.exception.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
public class CreditExceptionHandler {
    @ExceptionHandler(CreditCreationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleLoanNotFoundException(CreditCreationException ex) {
        return ExceptionResponse.builder().message(ex.getMessage()).build();
    }

    @ExceptionHandler(CreditCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleLoanCreationException(CreditCreationException ex) {
        return ExceptionResponse.builder().message(ex.getMessage()).build();
    }
}

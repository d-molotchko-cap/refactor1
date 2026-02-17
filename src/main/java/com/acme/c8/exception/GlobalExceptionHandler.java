package com.acme.c8.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // TODO: Controllers should not use try/catch.
    // Use centralized exception handling with @RestControllerAdvice.

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception ex) {

        // TODO: Return a proper error response (code, message, timestamp)

        return ResponseEntity.internalServerError()
                .body("Unexpected error occurred");
    }
}

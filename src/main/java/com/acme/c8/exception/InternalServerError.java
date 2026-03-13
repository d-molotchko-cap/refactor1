package com.acme.c8.exception;

public class InternalServerError extends RuntimeException {

    public InternalServerError(String message) {
        super(message);
    }

    public InternalServerError(String message, Throwable cause) {
        super(message, cause);
    }
}

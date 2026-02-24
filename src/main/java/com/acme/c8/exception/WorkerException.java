package com.acme.c8.exception;

import lombok.Getter;

@Getter
public class WorkerException extends RuntimeException {

    private final String errorCode;

    public WorkerException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public WorkerException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}

package com.acme.c8.exception;

public class DmnEvaluationException extends WorkerException {

    private static final String ERROR_CODE = "ERR_DMN_EVALUATION";

    public DmnEvaluationException(String message) {
        super(ERROR_CODE, message);
    }

    public DmnEvaluationException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}

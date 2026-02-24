package com.acme.c8.exception;

public class PatientLoadException extends WorkerException {

    private static final String ERROR_CODE = "ERR_PATIENT_LOAD";

    public PatientLoadException(String message) {
        super(ERROR_CODE, message);
    }

    public PatientLoadException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}

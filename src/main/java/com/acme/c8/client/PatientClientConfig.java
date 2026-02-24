package com.acme.c8.client;

import com.acme.c8.exception.PatientLoadException;
import feign.codec.ErrorDecoder;

public class PatientClientConfig {

    public ErrorDecoder errorDecoder() {
        return (methodKey, response) ->
                new PatientLoadException("Failed to load patients. HTTP " + response.status());
    }
}

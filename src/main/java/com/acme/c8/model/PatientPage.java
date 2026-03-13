package com.acme.c8.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PatientPage(
        List<Patient> content,
        long totalElements,
        int totalPages,
        int size,
        int number
) {}

package com.acme.c8.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Patient(
        Long id,
        String memberId,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String gender,
        String address,
        String city,
        String state,
        String zipCode,
        Double bmi,
        Double glucoseLevel,
        Double cholesterolLevel,
        Boolean hasDiabetes,
        Boolean hasHypertension,
        Boolean hasCopd,
        Integer erVisitsLast12Months,
        Boolean medicationAdherent,
        Boolean metabolicSyndromeRisk,
        Boolean highReadmissionRisk,
        Boolean medicationNonAdherenceRisk,
        String riskLevel
) {}

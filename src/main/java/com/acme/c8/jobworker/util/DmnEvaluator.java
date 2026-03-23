package com.acme.c8.jobworker.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.acme.c8.jobworker.PatientClient.loadPatients;

/**
 * DMN (Decision Model and Notation) evaluator.
 * Provides utilities for evaluating DMN decisions with caching.
 */
@Slf4j
public class DmnEvaluator {

    private static final String PATIENT_RULE_FILE = "PatientRule.dmn";
    private static final String PATIENT_RISK_DECISION_ID = "DeterminePatientRiskLevel";

    private static final DefaultDmnEngineConfiguration CONFIG =
            (DefaultDmnEngineConfiguration) DefaultDmnEngineConfiguration.createDefaultDmnEngineConfiguration();

    private static final DmnEngine DMN_ENGINE = CONFIG.buildEngine();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Cache of parsed DMN decisions.
     * Key format: dmnFile::decisionId
     */
    private static final Map<String, DmnDecision> DECISION_CACHE = new ConcurrentHashMap<>();

    private DmnEvaluator() {
        // Utility class - no instantiation
    }

    /**
     * Evaluate a DMN decision and return the result as JSON.
     */
    public static String evaluateToJson(
            String dmnFile,
            String decisionId,
            Map<String, Object> inputVariables) {

        DmnDecision decision = getOrLoadDecision(dmnFile, decisionId);
        VariableMap variables = Variables.createVariables();
        inputVariables.forEach(variables::putValue);

        DmnDecisionResult result = DMN_ENGINE.evaluateDecision(decision, variables);
        return toJson(result);
    }

    /**
     * Evaluate a DMN decision for a list of input maps.
     */
    public static String evaluateToJsonForList(
            String dmnFile,
            String decisionId,
            List<Map<String, Object>> inputs) {

        List<DmnDecisionResult> results = new ArrayList<>();
        DmnDecision decision = getOrLoadDecision(dmnFile, decisionId);

        for (Map<String, Object> inputVariables : inputs) {
            VariableMap variables = Variables.createVariables();
            inputVariables.forEach(variables::putValue);
            
            DmnDecisionResult result = DMN_ENGINE.evaluateDecision(decision, variables);
            results.add(result);
        }

        return toJsonFromList(results);
    }

    /**
     * Load and cache a DMN decision.
     */
    private static DmnDecision getOrLoadDecision(String dmnFile, String decisionId) {
        String cacheKey = dmnFile + "::" + decisionId;

        return DECISION_CACHE.computeIfAbsent(cacheKey, key -> {
            InputStream dmnStream = DmnEvaluator.class.getClassLoader().getResourceAsStream(dmnFile);

            if (dmnStream == null) {
                throw new IllegalArgumentException("DMN file not found on classpath: " + dmnFile);
            }

            log.info("Loading DMN decision: {} from file: {}", decisionId, dmnFile);
            return DMN_ENGINE.parseDecision(decisionId, dmnStream);
        });
    }

    /**
     * Convert a DMN decision result into JSON.
     */
    private static String toJson(DmnDecisionResult result) {
        try {
            return OBJECT_MAPPER.writeValueAsString(result.getResultList());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize DMN result to JSON", e);
            throw new RuntimeException("Failed to serialize DMN result to JSON", e);
        }
    }

    /**
     * Convert a list of DMN decision results into JSON.
     */
    private static String toJsonFromList(List<DmnDecisionResult> results) {
        try {
            return OBJECT_MAPPER.writeValueAsString(
                    results.stream()
                            .flatMap(r -> r.getResultList().stream())
                            .toList()
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize DMN results to JSON", e);
            throw new RuntimeException("Failed to serialize DMN results to JSON", e);
        }
    }

    /**
     * Evaluate patient risk level for a list of patients.
     */
    public static long go(int pageIndex) throws Exception {
        log.info("Starting patient risk evaluation for page index: {}", pageIndex);
        
        List<Map<String, Object>> patientList = loadPatients(pageIndex, 1000);
        int patientCount = patientList.size();
        log.info("Loaded {} patients", patientCount);

        long startTime = System.currentTimeMillis();
        String ruleResult = evaluateToJsonForList(PATIENT_RULE_FILE, PATIENT_RISK_DECISION_ID, patientList);
        long endTime = System.currentTimeMillis();

        long durationSeconds = (endTime - startTime) / 1000;
        log.info("Patient risk evaluation completed in {} seconds", durationSeconds);
        
        return durationSeconds;
    }

    // ============ Sample Data Methods ============

    /**
     * Create a sample patient with high risk profile.
     */
    public static Map<String, Object> getSamplePatientHighRisk() {
        Map<String, Object> patient = new HashMap<>();
        patient.put("id", 1L);
        patient.put("memberId", "M-1108257d01d14a11946f1a102ef22a91");
        patient.put("firstName", "Charlotte");
        patient.put("lastName", "Brown");
        patient.put("dateOfBirth", LocalDate.parse("1976-07-10"));
        patient.put("gender", "Non-binary");
        patient.put("address", "9552 Oak St");
        patient.put("city", "Boston");
        patient.put("state", "MA");
        patient.put("zipCode", "87785");
        patient.put("bmi", 37.9);
        patient.put("glucoseLevel", 146.7);
        patient.put("cholesterolLevel", 202.5);
        patient.put("hasDiabetes", true);
        patient.put("hasHypertension", true);
        patient.put("hasCopd", false);
        patient.put("erVisitsLast12Months", 6);
        patient.put("medicationAdherent", true);
        patient.put("metabolicSyndromeRisk", true);
        patient.put("highReadmissionRisk", true);
        patient.put("medicationNonAdherenceRisk", false);
        patient.put("riskLevel", "High");
        return patient;
    }

    /**
     * Create a sample patient with low risk profile.
     */
    public static Map<String, Object> getSamplePatientLowRisk() {
        Map<String, Object> patient = new HashMap<>();
        patient.put("id", 1L);
        patient.put("memberId", "M-1108257d01d14a11946f1a102ef22a91");
        patient.put("firstName", "Charlotte");
        patient.put("lastName", "Brown");
        patient.put("dateOfBirth", LocalDate.parse("1976-07-10"));
        patient.put("gender", "Non-binary");
        patient.put("address", "9552 Oak St");
        patient.put("city", "Boston");
        patient.put("state", "MA");
        patient.put("zipCode", "87785");
        patient.put("bmi", 20);
        patient.put("glucoseLevel", 100);
        patient.put("cholesterolLevel", 202.5);
        patient.put("hasDiabetes", false);
        patient.put("hasHypertension", false);
        patient.put("hasCopd", false);
        patient.put("erVisitsLast12Months", 0);
        patient.put("medicationAdherent", true);
        patient.put("metabolicSyndromeRisk", true);
        patient.put("highReadmissionRisk", true);
        return patient;
    }
}

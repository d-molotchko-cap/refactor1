package com.acme.c8.util;

import com.fasterxml.jackson.databind.ObjectMapper;
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

public class DmnEvaluator {

    private static final DefaultDmnEngineConfiguration CONFIG =
            (DefaultDmnEngineConfiguration)
                    DefaultDmnEngineConfiguration.createDefaultDmnEngineConfiguration();

    private static final DmnEngine DMN_ENGINE =
            CONFIG.buildEngine();

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper();

    /**
     * Cache of parsed DMN decisions.
     * Key = dmnFile + "::" + decisionId
     */
    private static final Map<String, DmnDecision> DECISION_CACHE =
            new ConcurrentHashMap<>();

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

        DmnDecisionResult result =
                DMN_ENGINE.evaluateDecision(decision, variables);

        return toJson(result);
    }

    public static String evaluateToJsonForList(
            String dmnFile,
            String decisionId,
            List<Map<String, Object>> pList) {

        List<DmnDecisionResult> resList = new ArrayList<DmnDecisionResult>();
        DmnDecision decision = getOrLoadDecision(dmnFile, decisionId);

        VariableMap variables = Variables.createVariables();
        for (Map<String, Object> inputVariables : pList) {
            inputVariables.forEach(variables::putValue);

            DmnDecisionResult result =
                    DMN_ENGINE.evaluateDecision(decision, variables);

            resList.add(result);
        }
        return toJsonFromList(resList);
    }

    /**
     * Load and cache a DMN decision if not already cached.
     */
    private static DmnDecision getOrLoadDecision(
            String dmnFile,
            String decisionId) {

        String cacheKey = dmnFile + "::" + decisionId;

        return DECISION_CACHE.computeIfAbsent(cacheKey, key -> {

            InputStream dmnStream = DmnEvaluator.class
                    .getClassLoader()
                    .getResourceAsStream(dmnFile);

            if (dmnStream == null) {
                throw new IllegalArgumentException(
                        "DMN file not found on classpath: " + dmnFile
                );
            }

            return DMN_ENGINE.parseDecision(decisionId, dmnStream);
        });
    }

    /**
     * Convert a DMN decision result into JSON.
     */
    private static String toJson(DmnDecisionResult result) {
        try {
            return OBJECT_MAPPER.writeValueAsString(
                    result.getResultList()
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to serialize DMN result to JSON", e
            );
        }
    }

    private static String toJsonFromList(List<DmnDecisionResult> results) {
        try {
            return OBJECT_MAPPER.writeValueAsString(
                    results.stream()
                            .flatMap(r -> r.getResultList().stream())
                            .toList()
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to serialize DMN results to JSON", e
            );
        }
    }

    /* -------------------------
       DEMO
       ------------------------- */

    public static Map<String, Object> getSampplePatnet() {
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

    public static Map<String, Object> getSampplePatnetLow() {
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
     //   patient.put("medicationNonAdherenceRisk", false);
    //    patient.put("riskLevel", "High");

        return patient;
    }

    public static void main(String[] args) throws Exception {
        go(List.of(getSampplePatnet(), getSampplePatnetLow()));
    }

    public static long go(List<Map<String, Object>> patientList) throws Exception {
        int size = patientList.size();
        System.out.println("Loaded patients: " + size);
        String patientRuleFile = "PatientRule.dmn";
        String did = "DeterminePatientRiskLevel";

        long start = System.currentTimeMillis();
        String ruleResult = evaluateToJsonForList(patientRuleFile, did, patientList);
        long end = System.currentTimeMillis();

        long duration = (end - start) / 1000;

      //  System.out.println("Time taken to evaluate in seconds: " + duration);
    //    System.out.println(ruleResult);

        return duration;
    }
}

package com.acme.c8.worker.dmn;

import com.acme.c8.client.PatientFeignClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DmnEvaluator {

    private final PatientFeignClient patientFeignClient;
    private final ObjectMapper objectMapper;

    private static final DefaultDmnEngineConfiguration CONFIG =
            (DefaultDmnEngineConfiguration) DefaultDmnEngineConfiguration.createDefaultDmnEngineConfiguration();

    private static final DmnEngine DMN_ENGINE = CONFIG.buildEngine();

    private static final Map<String, DmnDecision> DECISION_CACHE = new ConcurrentHashMap<>();

    public DmnEvaluator(PatientFeignClient patientFeignClient, ObjectMapper objectMapper) {
        this.patientFeignClient = patientFeignClient;
        this.objectMapper = objectMapper;
    }

    public String evaluateToJson(String dmnFile, String decisionId, Map<String, Object> inputVariables) {
        DmnDecision decision = getOrLoadDecision(dmnFile, decisionId);
        VariableMap variables = Variables.createVariables();
        inputVariables.forEach(variables::putValue);
        DmnDecisionResult result = DMN_ENGINE.evaluateDecision(decision, variables);
        return toJson(result);
    }

    public String evaluateToJsonForList(String dmnFile, String decisionId, List<Map<String, Object>> patientList) {
        List<DmnDecisionResult> results = new ArrayList<>();
        DmnDecision decision = getOrLoadDecision(dmnFile, decisionId);
        VariableMap variables = Variables.createVariables();
        for (Map<String, Object> inputVariables : patientList) {
            inputVariables.forEach(variables::putValue);
            results.add(DMN_ENGINE.evaluateDecision(decision, variables));
        }
        return toJsonFromList(results);
    }

    public long evaluatePatients(int pageIndex) {
        List<Map<String, Object>> patientList = patientFeignClient.loadPatients(pageIndex, 1000)
                .content()
                .stream()
                .map(p -> objectMapper.convertValue(p, new TypeReference<Map<String, Object>>() {}))
                .toList();

        log.info("Loaded {} patients for evaluation", patientList.size());

        long start = System.currentTimeMillis();
        evaluateToJsonForList("PatientRule.dmn", "DeterminePatientRiskLevel", patientList);
        long end = System.currentTimeMillis();

        return (end - start) / 1000;
    }

    private DmnDecision getOrLoadDecision(String dmnFile, String decisionId) {
        String cacheKey = dmnFile + "::" + decisionId;
        return DECISION_CACHE.computeIfAbsent(cacheKey, key -> {
            InputStream dmnStream = DmnEvaluator.class.getClassLoader().getResourceAsStream(dmnFile);
            if (dmnStream == null) {
                throw new IllegalArgumentException("DMN file not found on classpath: " + dmnFile);
            }
            return DMN_ENGINE.parseDecision(decisionId, dmnStream);
        });
    }

    private String toJson(DmnDecisionResult result) {
        try {
            return objectMapper.writeValueAsString(result.getResultList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize DMN result to JSON", e);
        }
    }

    private String toJsonFromList(List<DmnDecisionResult> results) {
        try {
            return objectMapper.writeValueAsString(
                    results.stream().flatMap(r -> r.getResultList().stream()).toList()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize DMN results to JSON", e);
        }
    }
}

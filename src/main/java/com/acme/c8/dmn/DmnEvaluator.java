package com.acme.c8.dmn;

import com.acme.c8.exception.InternalServerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DmnEvaluator {

    private static final DefaultDmnEngineConfiguration CONFIG =
            (DefaultDmnEngineConfiguration) DefaultDmnEngineConfiguration.createDefaultDmnEngineConfiguration();

    private static final DmnEngine DMN_ENGINE = CONFIG.buildEngine();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Map<String, DmnDecision> DECISION_CACHE = new ConcurrentHashMap<>();

    public static String evaluateToJson(String dmnFile, String decisionId, Map<String, Object> inputVariables) {
        DmnDecision decision = getOrLoadDecision(dmnFile, decisionId);

        VariableMap variables = Variables.createVariables();
        inputVariables.forEach(variables::putValue);

        DmnDecisionResult result = DMN_ENGINE.evaluateDecision(decision, variables);

        return toJson(result);
    }

    public static String evaluateToJsonForList(String dmnFile, String decisionId, List<Map<String, Object>> pList) {
        List<DmnDecisionResult> resList = new ArrayList<>();
        DmnDecision decision = getOrLoadDecision(dmnFile, decisionId);

        VariableMap variables = Variables.createVariables();
        for (Map<String, Object> inputVariables : pList) {
            inputVariables.forEach(variables::putValue);
            resList.add(DMN_ENGINE.evaluateDecision(decision, variables));
        }
        return toJsonFromList(resList);
    }

    private static DmnDecision getOrLoadDecision(String dmnFile, String decisionId) {
        String cacheKey = dmnFile + "::" + decisionId;

        return DECISION_CACHE.computeIfAbsent(cacheKey, key -> {
            InputStream dmnStream = DmnEvaluator.class
                    .getClassLoader()
                    .getResourceAsStream(dmnFile);

            if (dmnStream == null) {
                throw new InternalServerException("DMN file not found on classpath: " + dmnFile);
            }

            return DMN_ENGINE.parseDecision(decisionId, dmnStream);
        });
    }

    private static String toJson(DmnDecisionResult result) {
        try {
            return OBJECT_MAPPER.writeValueAsString(result.getResultList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize DMN result to JSON", e);
        }
    }

    private static String toJsonFromList(List<DmnDecisionResult> results) {
        try {
            return OBJECT_MAPPER.writeValueAsString(
                    results.stream().flatMap(r -> r.getResultList().stream()).toList()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize DMN results to JSON", e);
        }
    }
}

package com.acme.c8.util;

import com.acme.c8.client.PatientClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class DmnEvaluator {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final DmnEngine dmnEngine;
    private final PatientClient patientClient;

    private final Map<String, DmnDecision> decisionCache = new ConcurrentHashMap<>();

    public String evaluateToJson(
            String dmnFile,
            String decisionId,
            Map<String, Object> inputVariables) {

        DmnDecision decision = getOrLoadDecision(dmnFile, decisionId);

        VariableMap variables = Variables.createVariables();
        inputVariables.forEach(variables::putValue);

        DmnDecisionResult result = dmnEngine.evaluateDecision(decision, variables);
        return toJson(result);
    }

    public String evaluateToJsonForList(
            String dmnFile,
            String decisionId,
            List<Map<String, Object>> pList) {

        List<DmnDecisionResult> resList = new ArrayList<>();
        DmnDecision decision = getOrLoadDecision(dmnFile, decisionId);

        VariableMap variables = Variables.createVariables();
        for (Map<String, Object> inputVariables : pList) {
            inputVariables.forEach(variables::putValue);
            resList.add(dmnEngine.evaluateDecision(decision, variables));
        }
        return toJsonFromList(resList);
    }

    public long go(int pageIndex) throws Exception {
        List<Map<String, Object>> patientList = patientClient.loadPatients(pageIndex, 1000);
        log.debug("Loaded {} patients for page index {}", patientList.size(), pageIndex);

        String patientRuleFile = "PatientRule.dmn";
        String did = "DeterminePatientRiskLevel";

        long start = System.currentTimeMillis();
        evaluateToJsonForList(patientRuleFile, did, patientList);
        long end = System.currentTimeMillis();

        return (end - start) / 1000;
    }

    private DmnDecision getOrLoadDecision(String dmnFile, String decisionId) {
        String cacheKey = dmnFile + "::" + decisionId;
        return decisionCache.computeIfAbsent(cacheKey, key -> {
            InputStream dmnStream = DmnEvaluator.class
                    .getClassLoader()
                    .getResourceAsStream(dmnFile);
            if (dmnStream == null) {
                throw new IllegalArgumentException("DMN file not found on classpath: " + dmnFile);
            }
            return dmnEngine.parseDecision(decisionId, dmnStream);
        });
    }

    private String toJson(DmnDecisionResult result) {
        try {
            return OBJECT_MAPPER.writeValueAsString(result.getResultList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize DMN result to JSON", e);
        }
    }

    private String toJsonFromList(List<DmnDecisionResult> results) {
        try {
            return OBJECT_MAPPER.writeValueAsString(
                    results.stream().flatMap(r -> r.getResultList().stream()).toList()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize DMN results to JSON", e);
        }
    }
}

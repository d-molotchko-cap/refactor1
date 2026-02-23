package com.acme.c8.util;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class DmnAndFeelEvaluator {

    private final DmnEngine dmnEngine;

    public boolean evaluateUserIsFound(String userId) {

        InputStream dmnStream = DmnAndFeelEvaluator.class
                .getClassLoader()
                .getResourceAsStream("UserIsFound.dmn");

        if (dmnStream == null) {
            throw new IllegalStateException("UserIsFound.dmn not found on classpath");
        }

        DmnDecision decision = dmnEngine.parseDecision("UserIsFoundRule", dmnStream);

        VariableMap variables = Variables.createVariables()
                .putValue("userId", userId);

        DmnDecisionResult result = dmnEngine.evaluateDecision(decision, variables);

        return result.getSingleResult().getEntry("isFound");
    }
}

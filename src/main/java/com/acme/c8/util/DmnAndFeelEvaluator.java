package com.acme.c8.util;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

import java.io.InputStream;

public class DmnAndFeelEvaluator {

    private static final DefaultDmnEngineConfiguration CONFIG =
            (DefaultDmnEngineConfiguration) DefaultDmnEngineConfiguration.createDefaultDmnEngineConfiguration();

    private static final DmnEngine DMN_ENGINE = CONFIG.buildEngine();

    public static boolean evaluateUserIsFound(String userId) {

        InputStream dmnStream = DmnAndFeelEvaluator.class
                .getClassLoader()
                .getResourceAsStream("UserIsFound.dmn");

        if (dmnStream == null) {
            throw new IllegalStateException("UserIsFound.dmn not found on classpath");
        }

        DmnDecision decision =
                DMN_ENGINE.parseDecision("UserIsFoundRule", dmnStream);

        VariableMap variables = Variables.createVariables()
                .putValue("userId", userId);

        DmnDecisionResult result =
                DMN_ENGINE.evaluateDecision(decision, variables);

        return result
                .getSingleResult()
                .getEntry("isFound");
    }
}


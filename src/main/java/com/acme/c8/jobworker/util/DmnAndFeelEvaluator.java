package com.acme.c8.jobworker.util;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.feel.api.FeelEngineApi;
import org.camunda.feel.api.FeelEngineBuilder;

import java.io.InputStream;
import java.util.Map;

public class DmnAndFeelEvaluator {

    private static final DmnEngineConfiguration CONFIG =
            DmnEngineConfiguration.createDefaultDmnEngineConfiguration();

    private static final DmnEngine DMN_ENGINE =
            CONFIG.buildEngine();

    private static final FeelEngineApi FEEL_ENGINE_API =
            FeelEngineBuilder.forJava().build();

    /* -------------------------
       DMN
       ------------------------- */

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

        Object isFound = result.getSingleResult().getEntry("isFound");
        return Boolean.TRUE.equals(isFound);
    }

    /* -------------------------
       FEEL (ARBITRARY EXPRESSIONS)
       ------------------------- */

    public static Object evaluateFeel(
            String expression,
            Map<String, Object> variables) {
        var result = FEEL_ENGINE_API.evaluateExpression(expression, variables);
        return result.isFailure() ? null : result.result();
    }

    /* -------------------------
       DEMO
       ------------------------- */

    public static void main(String[] args) {

        System.out.println("DMN: " + evaluateUserIsFound("007"));

        System.out.println("FEEL 1: " +
                evaluateFeel(
                        "userId = \"007\"",
                        Map.of("userId", "007")
                )
        );

//        System.out.println("FEEL 2: " +
//                evaluateFeel(
//                        "if score >= 90 then \"A\" else \"B\"",
//                        Map.of("score", 95)
//                )
//        );
//
//        System.out.println("FEEL 3: " +
//                evaluateFeel(
//                        "sum(items)",
//                        Map.of("items", java.util.List.of(10, 20, 30))
//                )
//        );
    }
}

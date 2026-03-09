package com.acme.c8.jobworker.util;



import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.dmn.feel.impl.FeelEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.context.VariableContext;

import java.io.InputStream;
import java.util.Map;

@Slf4j
public class DmnAndFeelEvaluator {


    private static final DefaultDmnEngineConfiguration CONFIG = (DefaultDmnEngineConfiguration) DefaultDmnEngineConfiguration.createDefaultDmnEngineConfiguration();

    private static final DmnEngine DMN_ENGINE =
            CONFIG.buildEngine();

    // ✅ FEEL is now a supported public API in 7.24
    private static final FeelEngine FEEL_ENGINE =
            CONFIG.getFeelEngine();

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

        return result
                .getSingleResult()
                .getEntry("isFound");
    }

    /* -------------------------
       FEEL (ARBITRARY EXPRESSIONS)
       ------------------------- */

    public static Object evaluateFeel(
            String expression,
            Map<String, Object> variables) {

        VariableContext context = Variables.fromMap(variables).asVariableContext();
        return FEEL_ENGINE.evaluateSimpleExpression(expression, context);
    }

    /* -------------------------
       DEMO
       ------------------------- */

    public static void main(String[] args) {

        log.info("DMN: {}", evaluateUserIsFound("007"));

        log.info("FEEL 1: {}",
                evaluateFeel(
                        "userId = \"007\"",
                        Map.of("userId", "007")
                )
        );

        log.info("FEEL 2: {}",
                evaluateFeel(
                        "if score >= 90 then \"A\" else \"B\"",
                        Map.of("score", 95)
                )
        );

        log.info("FEEL 3: {}",
                evaluateFeel(
                        "sum(items)",
                        Map.of("items", java.util.List.of(10, 20, 30))
                )
        );
    }
}

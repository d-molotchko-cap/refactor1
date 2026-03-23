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

/**
 * Combined DMN and FEEL (Friendly Enough Expression Language) evaluator.
 * Supports both DMN decisions and arbitrary FEEL expressions.
 */
@Slf4j
public class DmnAndFeelEvaluator {

    private static final String USER_IS_FOUND_DMN = "UserIsFound.dmn";
    private static final String USER_IS_FOUND_DECISION_ID = "UserIsFoundRule";

    private static final DefaultDmnEngineConfiguration CONFIG = 
            (DefaultDmnEngineConfiguration) DefaultDmnEngineConfiguration.createDefaultDmnEngineConfiguration();

    private static final DmnEngine DMN_ENGINE = CONFIG.buildEngine();
    
    // FEEL is now a supported public API in Camunda 7.24+
    private static final FeelEngine FEEL_ENGINE = CONFIG.getFeelEngine();

    private DmnAndFeelEvaluator() {
        // Utility class - no instantiation
    }

    /**
     * Evaluate whether a user ID is found using DMN decision.
     *
     * @param userId the user ID to check
     * @return true if user is found, false otherwise
     * @throws IllegalStateException if DMN file is not found
     */
    public static boolean evaluateUserIsFound(String userId) {
        log.debug("Evaluating if user exists: {}", userId);
        
        InputStream dmnStream = DmnAndFeelEvaluator.class
                .getClassLoader()
                .getResourceAsStream(USER_IS_FOUND_DMN);

        if (dmnStream == null) {
            log.error("DMN file not found: {}", USER_IS_FOUND_DMN);
            throw new IllegalStateException(USER_IS_FOUND_DMN + " not found on classpath");
        }

        DmnDecision decision = DMN_ENGINE.parseDecision(USER_IS_FOUND_DECISION_ID, dmnStream);
        VariableMap variables = Variables.createVariables().putValue("userId", userId);
        
        DmnDecisionResult result = DMN_ENGINE.evaluateDecision(decision, variables);
        boolean isFound = result.getSingleResult().getEntry("isFound");
        
        log.debug("User {} found: {}", userId, isFound);
        return isFound;
    }

    /**
     * Evaluate a FEEL (Friendly Enough Expression Language) expression.
     *
     * @param expression the FEEL expression to evaluate
     * @param variables  the variables available to the expression
     * @return the result of the evaluation
     */
    public static Object evaluateFeel(String expression, Map<String, Object> variables) {
        try {
            log.debug("Evaluating FEEL expression: {}", expression);
            VariableContext context = VariableContext.fromMap(variables);
            Object result = FEEL_ENGINE.evaluateSimpleExpression(expression, context);
            log.debug("FEEL evaluation result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error evaluating FEEL expression: {}", expression, e);
            throw new RuntimeException("FEEL evaluation failed for expression: " + expression, e);
        }
    }
}

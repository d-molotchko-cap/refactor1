package com.acme.c8.worker.dmn;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.dmn.feel.impl.FeelEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.context.VariableContext;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

@Slf4j
@Component
public class DmnAndFeelEvaluator {

    private static final DefaultDmnEngineConfiguration CONFIG =
            (DefaultDmnEngineConfiguration) DefaultDmnEngineConfiguration.createDefaultDmnEngineConfiguration();

    private static final DmnEngine DMN_ENGINE = CONFIG.buildEngine();

    private static final FeelEngine FEEL_ENGINE = CONFIG.getFeelEngine();

    public boolean evaluateUserIsFound(String userId) {
        InputStream dmnStream = getClass().getClassLoader().getResourceAsStream("UserIsFound.dmn");
        if (dmnStream == null) {
            throw new IllegalStateException("UserIsFound.dmn not found on classpath");
        }
        DmnDecision decision = DMN_ENGINE.parseDecision("UserIsFoundRule", dmnStream);
        VariableMap variables = Variables.createVariables().putValue("userId", userId);
        DmnDecisionResult result = DMN_ENGINE.evaluateDecision(decision, variables);
        return result.getSingleResult().getEntry("isFound");
    }

    public Object evaluateFeelExpression(String expression, Map<String, Object> variables) {
        VariableContext context = null;
        return FEEL_ENGINE.evaluateSimpleExpression(expression, context);
    }
}

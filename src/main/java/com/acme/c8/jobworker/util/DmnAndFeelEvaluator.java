package com.acme.c8.jobworker.util;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.dmn.feel.impl.FeelEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

import java.io.InputStream;

@Slf4j
public final class DmnAndFeelEvaluator {


    private static final DefaultDmnEngineConfiguration CONFIG = (DefaultDmnEngineConfiguration) DefaultDmnEngineConfiguration.createDefaultDmnEngineConfiguration();

    private static final DmnEngine DMN_ENGINE =
            CONFIG.buildEngine();

    /* -------------------------
       DMN
       ------------------------- */

    public static boolean evaluateUserIsFound(String userId) {

        InputStream dmnStream = DmnAndFeelEvaluator.class
                .getClassLoader()
                .getResourceAsStream("UserIsFound.dmn");

        if (dmnStream == null) {
            return false;
        }

        try{

            DmnDecision decision =
                    DMN_ENGINE.parseDecision("UserIsFoundRule", dmnStream);

            VariableMap variables = Variables.createVariables()
                    .putValue("userId", userId);

            DmnDecisionResult result =
                    DMN_ENGINE.evaluateDecision(decision, variables);

            return result
                    .getSingleResult()
                    .getEntry("isFound");

        }catch (Exception e){
            log.error("Error when evaluating decision "+e.getMessage(), e);
            throw new RuntimeException("Error evaluating DMN decision: " + e.getMessage(), e);
        }

    }


}

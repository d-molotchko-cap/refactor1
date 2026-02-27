package com.acme.c8.jobworker;

import com.acme.c8.jobworker.util.DmnEvaluator;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.exception.BpmnError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;

@Slf4j
@Component
@AllArgsConstructor
public class JobWorkerJobWorker {

    private final JobWorkerJobWorkerService service;

    @JobWorker(type = "com.capbpm.c8.JobWorker.FindUser:v.1.1", fetchVariables = {"userId"})
    public Map<String, Object> findUser(final ActivatedJob job, @Variable String userId) {
        return executeWithErrorHandling(job, "JobWorker.findUser",
                () -> service.findUserImpl(userId));
    }

    @JobWorker(type = "com.capbpm.c8.JobWorker.filterPatients:v.1.1", fetchVariables = {"index"})
    public Map<String, Object> filterPatients(final ActivatedJob job, @Variable Integer index) {
        return executeWithErrorHandling(job, "JobWorker.filterPatients", () -> {
            long duration = 0;
            try {
                duration = DmnEvaluator.go(index);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            log.trace("duration={}", duration);
            return Map.of("duration", duration);
        });
    }

    private Map<String, Object> executeWithErrorHandling(
            ActivatedJob job,
            String methodName,
            Supplier<Map<String, Object>> businessLogic) {

        logStarted(methodName);

        try {
            Map<String, Object> outputs = businessLogic.get();
            logFinished(methodName);
            return outputs;
        } catch (Exception e) {
            logError(methodName);
            Map<String, Object> inputVarMap = job.getVariablesAsMap(); // ZeebeBpmnError is deprecated
            throw new BpmnError("ERR_CODE", e.getMessage());
        }
    }

    private void logError(String METHOD_NAME) {
        log.trace("{} Error.", METHOD_NAME);
    }

    private void logStarted(String METHOD_NAME) {
        log.trace("{} started...", METHOD_NAME);
    }

    private void logFinished(String METHOD_NAME) {
        log.trace("{} Finished.", METHOD_NAME);
    }
}



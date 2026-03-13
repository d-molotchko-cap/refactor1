package com.acme.c8.worker;

import com.acme.c8.exception.InternalServerError;
import com.acme.c8.services.PatientJobWorkerService;
import com.acme.c8.worker.dmn.DmnEvaluator;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PatientJobWorker {

    private final PatientJobWorkerService service;
    private final DmnEvaluator dmnEvaluator;

    @JobWorker(type = "com.capbpm.c8.JobWorker.FindUser:v.1.1", fetchVariables = {"userId"})
    public Map<String, Object> findUser(@Variable String userId) {
        log.trace("findUser started for userId: {}", userId);
        try {
            Map<String, Object> outputs = service.findUser(userId);
            log.trace("findUser completed for userId: {}", userId);
            return outputs;
        } catch (Exception e) {
            log.error("findUser failed for userId: {}", userId, e);
            throw new InternalServerError(e.getMessage(), e);
        }
    }

    @JobWorker(type = "com.capbpm.c8.JobWorker.filterPatients:v.1.1", fetchVariables = {"index"})
    public Map<String, Object> filterPatients(@Variable Integer index) {
        log.trace("filterPatients started for index: {}", index);
        try {
            long duration = dmnEvaluator.evaluatePatients(index);
            Map<String, Object> outputs = new HashMap<>();
            outputs.put("duration", duration);
            log.debug("filterPatients completed. duration={}s", duration);
            return outputs;
        } catch (Exception e) {
            log.error("filterPatients failed for index: {}", index, e);
            throw new InternalServerError(e.getMessage(), e);
        }
    }
}

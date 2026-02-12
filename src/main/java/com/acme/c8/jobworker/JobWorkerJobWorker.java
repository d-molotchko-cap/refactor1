package com.acme.c8.jobworker;

import com.acme.c8.jobworker.util.DmnEvaluator;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.client.exception.BpmnError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobWorkerJobWorker {

    private final JobWorkerJobWorkerService service;

    @JobWorker(type = "com.capbpm.c8.JobWorker.FindUser:v.1.1", fetchVariables = {"userId"})
    public Map<String, Object> findUser(@Variable String userId) {
        log.trace("JobWorker.findUser started... userId={}", userId);

        try {
            Map<String, Object> outputs = service.findUserImpl(userId);
            log.trace("JobWorker.findUser finished.");
            return outputs;
        } catch (Exception e) {
            log.trace("JobWorker.findUser error.", e);
            throw new BpmnError("ERR_CODE", e.getMessage());
        }
    }

    @JobWorker(type = "com.capbpm.c8.JobWorker.filterPatients:v.1.1", fetchVariables = {"index"})
    public Map<String, Object> sift(@Variable Integer index) {
        log.trace("JobWorker.filterPatients started... index={}", index);

        try {
           long duration= DmnEvaluator.go(index);
           Map<String, Object> outputs = new HashMap<>();
           outputs.put("duration", duration);

            log.trace("JobWorker.filterPatients finished.");
            return outputs;
        } catch (Exception e) {
            log.trace("JobWorker.filterPatients error. {}", e);
            throw new BpmnError("ERR_CODE", e.getMessage());
        }
    }
}



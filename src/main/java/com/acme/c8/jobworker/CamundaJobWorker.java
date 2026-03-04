package com.acme.c8.jobworker;

import com.acme.c8.jobworker.util.DmnEvaluator;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class CamundaJobWorker {

    private final CamundaJobWorkerService service;

    @JobWorker(type = "com.capbpm.c8.JobWorker.FindUser:v.1.1", fetchVariables = {"userId"})
    public Map<String, Object> findUser(final ActivatedJob job, @Variable String userId) {
        log.trace("findUser started...");

        try {
            Map<String, Object> outputs = service.findUserImpl(userId);
            log.trace("findUser finished.");
            return outputs;
        } catch (Exception e) {
            log.error("findUser error.", e);
            throw new ZeebeBpmnError("ERR_CODE", e.getMessage());
        }
    }

    @JobWorker(type = "com.capbpm.c8.JobWorker.filterPatients:v.1.1", fetchVariables = {"index"})
    public Map<String, Object> filterPatients(final ActivatedJob job, @Variable Integer index) {
        log.trace("filterPatients started...");
        Map<String, Object> inputVarMap = job.getVariablesAsMap();

        try {
            long duration = DmnEvaluator.evaluatePatientRules(index);
            Map<String, Object> outputs = new HashMap<>();
            outputs.put("duration", duration);
            log.info("filterPatients finished, duration={}s", duration);
            return outputs;
        } catch (Exception e) {
            log.error("filterPatients error.", e);
            throw new ZeebeBpmnError("ERR_CODE", e.getMessage());
        }
    }
}

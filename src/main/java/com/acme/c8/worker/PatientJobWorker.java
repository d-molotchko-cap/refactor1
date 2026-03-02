package com.acme.c8.worker;

import com.acme.c8.service.PatientService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class PatientJobWorker {

    private final PatientService service;

    @JobWorker(type = "com.capbpm.c8.JobWorker.FindUser:v.1.1", fetchVariables = {"userId"})
    public Map<String, Object> findUser(final ActivatedJob job, @Variable String userId) {
        final String METHOD_NAME = "JobWorker.findUser";
        Map<String, Object> inputVarMap = job.getVariablesAsMap();
        log.trace(METHOD_NAME + " started...");

        try {
            Map<String, Object> outputs = service.findUser(userId);

            log.trace(METHOD_NAME + " Finished.");
            return outputs;
        } catch (Exception e) {
            log.trace(METHOD_NAME + " Error.");
            throw new ZeebeBpmnError("ERR_CODE", e.getMessage(), inputVarMap);
        }
    }

    @JobWorker(type = "com.capbpm.c8.JobWorker.filterPatients:v.1.1", fetchVariables = {"index"})
    public Map<String, Object> sift(final ActivatedJob job, @Variable Integer index) {
        final String METHOD_NAME = "JobWorker.filterPatients";
        Map<String, Object> inputVarMap = job.getVariablesAsMap();
        log.trace(METHOD_NAME + " started...");

        try {
            long duration = service.evaluatePatients(index);

            Map<String, Object> outputs = new HashMap<>();
            outputs.put("duration", duration);
            log.info("duration=" + duration);

            // log.trace(METHOD_NAME + " Finished.");
            return outputs;//n outputs;
        } catch (Exception e) {
            log.trace(METHOD_NAME + " Error.");
            throw new ZeebeBpmnError("ERR_CODE", e.getMessage(), inputVarMap);
        }
    }
}

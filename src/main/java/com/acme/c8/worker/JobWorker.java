package com.acme.c8.worker;

import com.acme.c8.util.DmnEvaluator;
import com.acme.c8.client.PatientClient;
import com.acme.c8.worker.service.JobWorkerService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class JobWorker {

    private static final String ERR_CODE = "ERR_CODE";

    private final JobWorkerService service;
    private final PatientClient patientClient;

    public JobWorker(JobWorkerService service, PatientClient patientClient) {
        this.service = service;
        this.patientClient = patientClient;
    }

    @io.camunda.zeebe.spring.client.annotation.JobWorker(type = "com.capbpm.c8.JobWorker.FindUser:v.1.1", fetchVariables = {"userId"})
    public Map<String, Object> findUser(final ActivatedJob job, @Variable String userId) {
        final String methodName = "JobWorker.findUser";
        Map<String, Object> inputVarMap = job.getVariablesAsMap();
        log.trace("{} started...", methodName);

        try {
            Map<String, Object> outputs = service.findUserImpl(userId);
            log.trace("{} finished.", methodName);
            return outputs;
        } catch (Exception e) {
            log.trace("{} error.", methodName, e);
            throw new ZeebeBpmnError(ERR_CODE, e.getMessage(), inputVarMap);
        }
    }

    @io.camunda.zeebe.spring.client.annotation.JobWorker(type = "com.capbpm.c8.JobWorker.filterPatients:v.1.1", fetchVariables = {"index"})
    public Map<String, Object> sift(final ActivatedJob job, @Variable Integer index) {
        final String methodName = "JobWorker.sift";
        Map<String, Object> inputVarMap = job.getVariablesAsMap();
        log.trace("{} started...", methodName);

        try {
            long duration = DmnEvaluator.go(patientClient, index);

            Map<String, Object> outputs = new HashMap<>();
            outputs.put("duration", duration);
            log.debug("DMN patient evaluation durationSeconds={}", duration);

            return outputs;
        } catch (Exception e) {
            log.trace("{} error.", methodName, e);
            throw new ZeebeBpmnError(ERR_CODE, e.getMessage(), inputVarMap);
        }
    }
}


package com.acme.c8.jobworker;

import com.acme.c8.service.JobWorkerJobWorkerService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class JobWorkerJobWorker {

    private final JobWorkerJobWorkerService service;

    @JobWorker(type = "com.capbpm.c8.JobWorker.FindUser:v.1.1", fetchVariables = {"userId"})
    public Map<String, Object> findUser(final ActivatedJob job, @Variable String userId) {
        final String METHOD_NAME = "JobWorker.findUser";
        Map<String, Object> inputVarMap = job.getVariablesAsMap();
        log.debug("{} started", METHOD_NAME);

        try {
            Map<String, Object> outputs = service.findUserImpl(userId);
            log.debug("{} finished", METHOD_NAME);
            return outputs;
        } catch (Exception e) {
            log.error("{} failed", METHOD_NAME, e);
            throw new ZeebeBpmnError("ERR_CODE", e.getMessage(), inputVarMap);
        }
    }

    @JobWorker(type = "com.capbpm.c8.JobWorker.filterPatients:v.1.1", fetchVariables = {"index"})
    public Map<String, Object> sift(final ActivatedJob job, @Variable Integer index) {
        final String METHOD_NAME = "JobWorker.filterPatients";
        Map<String, Object> inputVarMap = job.getVariablesAsMap();
        log.debug("{} started", METHOD_NAME);

        try {
            Map<String, Object> outputs = service.filterPatients(index);
            log.info("{} completed, duration={}s", METHOD_NAME, outputs.get("duration"));
            return outputs;
        } catch (Exception e) {
            log.error("{} failed", METHOD_NAME, e);
            throw new ZeebeBpmnError("ERR_CODE", e.getMessage(), inputVarMap);
        }
    }
}

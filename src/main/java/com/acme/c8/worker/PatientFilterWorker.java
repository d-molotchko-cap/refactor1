package com.acme.c8.worker;

import com.acme.c8.client.PatientClient;
import com.acme.c8.dmn.DmnEvaluator;
import com.acme.c8.exception.DmnEvaluationException;
import com.acme.c8.exception.PatientLoadException;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PatientFilterWorker {

    private final PatientClient patientClient;

    @Value("${app.dmn.patient-rule-file}")
    private String patientRuleFile;

    @Value("${app.dmn.patient-decision-id}")
    private String patientDecisionId;

    @JobWorker(type = "${app.worker.filter-patients-type}", fetchVariables = {"index"})
    public Map<String, Object> filterPatients(final ActivatedJob job, @Variable Integer index) {
        final String METHOD_NAME = "PatientFilterWorker.filterPatients";
        Map<String, Object> inputVarMap = job.getVariablesAsMap();
        log.trace("{} started...", METHOD_NAME);

        try {
            List<Map<String, Object>> patientList = patientClient.loadPatients(index, 1000).getContent();

            long start = System.currentTimeMillis();
            DmnEvaluator.evaluateToJsonForList(patientRuleFile, patientDecisionId, patientList);
            long duration = (System.currentTimeMillis() - start) / 1000;

            Map<String, Object> outputs = new HashMap<>();
            outputs.put("duration", duration);
            log.info("{} duration={}s", METHOD_NAME, duration);
            log.trace("{} finished.", METHOD_NAME);
            return outputs;
        } catch (PatientLoadException e) {
            log.error("{} patient load error [code={}]: {}", METHOD_NAME, e.getErrorCode(), e.getMessage());
            throw new ZeebeBpmnError(e.getErrorCode(), e.getMessage(), inputVarMap);
        } catch (DmnEvaluationException e) {
            log.error("{} DMN evaluation error [code={}]: {}", METHOD_NAME, e.getErrorCode(), e.getMessage());
            throw new ZeebeBpmnError(e.getErrorCode(), e.getMessage(), inputVarMap);
        } catch (Exception e) {
            log.error("{} unexpected error: {}", METHOD_NAME, e.getMessage());
            throw new ZeebeBpmnError("ERR_FILTER_PATIENTS_UNEXPECTED", e.getMessage(), inputVarMap);
        }
    }
}

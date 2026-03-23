package com.acme.c8.jobworker;

import com.acme.c8.jobworker.util.DmnEvaluator;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Job Worker component for processing Camunda job tasks.
 * Handles user finding and patient filtering operations.
 */
@Slf4j
@Component
@AllArgsConstructor
public class JobWorkerJobWorker {

    private static final String ERROR_CODE = "ERR_CODE";
    private static final String FIND_USER_TYPE = "com.capbpm.c8.JobWorker.FindUser:v.1.1";
    private static final String FILTER_PATIENTS_TYPE = "com.capbpm.c8.JobWorker.filterPatients:v.1.1";

    private final JobWorkerJobWorkerService service;

    /**
     * Job worker for finding users by ID.
     *
     * @param job    the activated Camunda job
     * @param userId the user ID to search for
     * @return map containing the search result
     */
    @JobWorker(type = FIND_USER_TYPE, fetchVariables = {"userId"})
    public Map<String, Object> findUser(final ActivatedJob job, @Variable String userId) {
        log.debug("Starting findUser job worker with userId: {}", userId);
        
        try {
            Map<String, Object> outputs = service.findUserImpl(userId);
            log.debug("findUser job completed successfully");
            return outputs;
        } catch (Exception e) {
            log.error("Error in findUser job worker", e);
            throw new ZeebeBpmnError(ERROR_CODE, e.getMessage(), job.getVariablesAsMap());
        }
    }

    /**
     * Job worker for filtering and analyzing patients.
     *
     * @param job   the activated Camunda job
     * @param index the page index for patient data
     * @return map containing analysis duration
     */
    @JobWorker(type = FILTER_PATIENTS_TYPE, fetchVariables = {"index"})
    public Map<String, Object> sift(final ActivatedJob job, @Variable Integer index) {
        log.debug("Starting filterPatients job worker with index: {}", index);
        
        try {
            long duration = DmnEvaluator.go(index);
            
            Map<String, Object> outputs = new HashMap<>();
            outputs.put("duration", duration);
            log.debug("Patient filtering completed in {} seconds", duration);
            
            return outputs;
        } catch (Exception e) {
            log.error("Error in filterPatients job worker", e);
            throw new ZeebeBpmnError(ERROR_CODE, e.getMessage(), job.getVariablesAsMap());
        }
    }
}



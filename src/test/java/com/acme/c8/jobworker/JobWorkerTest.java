package com.acme.c8.jobworker;

import com.acme.c8.util.DmnEvaluator;
import com.acme.c8.client.PatientClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class JobWorkerTest {

    @Test
    void sift_returnsDurationFromDmnEvaluator() throws Exception {
        JobWorkerJobWorkerService service = mock(JobWorkerJobWorkerService.class);
        PatientClient patientClient = mock(PatientClient.class);
        ActivatedJob job = mock(ActivatedJob.class);

        when(job.getVariablesAsMap()).thenReturn(Collections.emptyMap());

        try (MockedStatic<DmnEvaluator> evaluatorMock = mockStatic(DmnEvaluator.class)) {
            evaluatorMock.when(() -> DmnEvaluator.go(patientClient, 0))
                    .thenReturn(42L);

            JobWorkerJobWorker worker = new JobWorkerJobWorker(service, patientClient);

            Map<String, Object> result = worker.sift(job, 0);

            assertEquals(42L, result.get("duration"));
        }
    }

    @Test
    void sift_wrapsExceptionsInZeebeBpmnError() throws Exception {
        JobWorkerJobWorkerService service = mock(JobWorkerJobWorkerService.class);
        PatientClient patientClient = mock(PatientClient.class);
        ActivatedJob job = mock(ActivatedJob.class);

        when(job.getVariablesAsMap()).thenReturn(Collections.emptyMap());

        try (MockedStatic<DmnEvaluator> evaluatorMock = mockStatic(DmnEvaluator.class)) {
            evaluatorMock.when(() -> DmnEvaluator.go(patientClient, 0))
                    .thenThrow(new RuntimeException("boom"));

            JobWorkerJobWorker worker = new JobWorkerJobWorker(service, patientClient);

            assertThrows(ZeebeBpmnError.class, () -> worker.sift(job, 0));
        }
    }
}


package com.acme.c8.worker;

import com.acme.c8.services.PatientJobWorkerService;
import com.acme.c8.worker.dmn.DmnAndFeelEvaluator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientJobWorkerServiceTest {

    @Mock
    private DmnAndFeelEvaluator dmnAndFeelEvaluator;

    @InjectMocks
    private PatientJobWorkerService service;

    @Test
    void findUser_whenUserFound_returnsIsFoundTrue() {
        when(dmnAndFeelEvaluator.evaluateUserIsFound("007")).thenReturn(true);

        Map<String, Object> result = service.findUser("007");

        assertNotNull(result);
        assertEquals(true, result.get("isFound"));
        verify(dmnAndFeelEvaluator).evaluateUserIsFound("007");
    }

    @Test
    void findUser_whenUserNotFound_returnsIsFoundFalse() {
        when(dmnAndFeelEvaluator.evaluateUserIsFound("unknown-user")).thenReturn(false);

        Map<String, Object> result = service.findUser("unknown-user");

        assertNotNull(result);
        assertEquals(false, result.get("isFound"));
    }

    @Test
    void findUser_resultMapContainsOnlyIsFoundKey() {
        when(dmnAndFeelEvaluator.evaluateUserIsFound("any")).thenReturn(true);

        Map<String, Object> result = service.findUser("any");

        assertEquals(1, result.size());
        assertTrue(result.containsKey("isFound"));
    }
}

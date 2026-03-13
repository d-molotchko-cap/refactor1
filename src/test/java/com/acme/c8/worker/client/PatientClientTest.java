package com.acme.c8.worker.client;

import com.acme.c8.client.PatientFeignClient;
import com.acme.c8.model.Patient;
import com.acme.c8.model.PatientPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientClientTest {

    @Mock
    private PatientFeignClient patientFeignClient;

    @Test
    void loadPatients_returnsPageWithContent() {
        Patient patient = buildPatient(1L, "High");
        PatientPage mockPage = new PatientPage(List.of(patient), 1, 1, 25, 0);
        when(patientFeignClient.loadPatients(0, 25)).thenReturn(mockPage);

        PatientPage result = patientFeignClient.loadPatients(0, 25);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals("High", result.content().get(0).riskLevel());
        verify(patientFeignClient).loadPatients(0, 25);
    }

    @Test
    void loadPatients_emptyPage_returnsEmptyContent() {
        PatientPage mockPage = new PatientPage(List.of(), 0, 0, 25, 0);
        when(patientFeignClient.loadPatients(0, 25)).thenReturn(mockPage);

        PatientPage result = patientFeignClient.loadPatients(0, 25);

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.totalElements());
    }

    private Patient buildPatient(Long id, String riskLevel) {
        return new Patient(
                id, "M-001", "John", "Doe", LocalDate.of(1980, 1, 1),
                "Male", "123 Main St", "Boston", "MA", "02101",
                32.0, 145.0, 210.0, true, true, false,
                3, true, true, true, false, riskLevel
        );
    }
}

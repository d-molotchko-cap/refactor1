package com.acme.c8.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientClientTest {

    @Mock
    private PatientClient patientClient;

    @Test
    void loadPatients_returnsExpectedSizeAndRiskLevel() {
        PatientPage page = new PatientPage();
        page.setContent(List.of(
                Map.of("id", 1, "firstName", "Charlotte", "lastName", "Brown", "riskLevel", "High"),
                Map.of("id", 2, "firstName", "John",      "lastName", "Doe",   "riskLevel", "Low")
        ));
        when(patientClient.loadPatients(0, 25)).thenReturn(page);

        List<Map<String, Object>> patients = patientClient.loadPatients(0, 25).getContent();

        assertNotNull(patients, "Patient list must not be null");
        assertFalse(patients.isEmpty(), "Patient list must not be empty");
        assertEquals(2, patients.size(), "Should have loaded 2 patients");
        assertEquals("High", patients.get(0).get("riskLevel"), "First patient riskLevel should be High");
    }

    @Test
    void loadPatients_emptyContent_returnsEmptyList() {
        PatientPage page = new PatientPage();
        page.setContent(List.of());
        when(patientClient.loadPatients(0, 25)).thenReturn(page);

        List<Map<String, Object>> patients = patientClient.loadPatients(0, 25).getContent();

        assertNotNull(patients);
        assertTrue(patients.isEmpty(), "Content should be empty");
    }
}

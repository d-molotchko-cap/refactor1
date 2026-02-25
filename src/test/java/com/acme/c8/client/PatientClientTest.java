package com.acme.c8.client;

import com.acme.c8.configurations.PatientsApiProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PatientClientTest {

    @Test
    void loadPatients_parsesContentArray() throws Exception {
        String body = """
                {
                  "content": [
                    { "id": 1, "riskLevel": "High" },
                    { "id": 2, "riskLevel": "Low" }
                  ]
                }
                """;

        ExchangeFunction exchangeFunction = request ->
                Mono.just(ClientResponse
                        .create(HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(body.getBytes(StandardCharsets.UTF_8))
                        .build());

        WebClient.Builder builder = WebClient.builder().exchangeFunction(exchangeFunction);

        PatientsApiProperties properties = new PatientsApiProperties();
        properties.setBaseUrl("http://example.org");
        properties.setPath("/api/patients/load");
        properties.setDefaultPageSize(25);

        PatientClient client = new PatientClient(builder, properties);

        List<Map<String, Object>> patients = client.loadPatients(0, 25);

        assertNotNull(patients);
        assertEquals(2, patients.size());
        assertEquals("High", patients.get(0).get("riskLevel"));
    }
}


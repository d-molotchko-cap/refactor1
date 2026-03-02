package com.acme.c8.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PatientClient {

    @Value("${patient.api.base-url}")
    private String baseUrl;

    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper mapper;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Calls the patients API and returns the "content" array
     * as a List<Map<String, Object>>.
     */
    public List<Map<String, Object>> loadPatients(int page, int size) {
        JsonNode root = restClient.get()
                .uri("/api/patients/load?page={page}&size={size}", page, size)
                .retrieve()
                .body(JsonNode.class);

        if (root == null) {
            throw new IllegalStateException("Empty response from patients API");
        }

        // Extract "content" array
        JsonNode contentNode = root.get("content");
        if (contentNode == null || !contentNode.isArray()) {
            throw new IllegalStateException("Response does not contain a valid 'content' array");
        }

        // Convert to List<Map<String, Object>>
        return mapper.convertValue(
                contentNode,
                new TypeReference<List<Map<String, Object>>>() {}
        );
    }

    // Example usage
    public static void main(String[] args) throws Exception {
        PatientClient client = new PatientClient(RestClient.builder(), new ObjectMapper());
        client.baseUrl = "https://api.capbpm.com";
        client.init();
        List<Map<String, Object>> patients = client.loadPatients(0, 25);

        System.out.println("Loaded patients: " + patients.size());
        System.out.println("First patient riskLevel: " + patients.get(0).get("riskLevel"));
    }
}

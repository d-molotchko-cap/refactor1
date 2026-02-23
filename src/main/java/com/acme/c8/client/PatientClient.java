package com.acme.c8.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PatientClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final RestClient restClient;

    public PatientClient(RestClient.Builder builder,
                         @Value("${patient.api.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public List<Map<String, Object>> loadPatients(int page, int size) {
        log.debug("Loading patients page={} size={}", page, size);

        String body = restClient
                .get()
                .uri("/api/patients/load?page={page}&size={size}", page, size)
                .retrieve()
                .body(String.class);

        JsonNode root = MAPPER.readTree(body);
        JsonNode contentNode = root.get("content");

        if (contentNode == null || !contentNode.isArray()) {
            throw new IllegalStateException("Response does not contain a valid 'content' array");
        }

        return MAPPER.convertValue(
                contentNode,
                new TypeReference<List<Map<String, Object>>>() {}
        );
    }
}

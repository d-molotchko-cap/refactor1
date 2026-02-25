package com.acme.c8.client;

import com.acme.c8.configurations.PatientsApiProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class PatientClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final WebClient webClient;
    private final PatientsApiProperties properties;

    public PatientClient(WebClient.Builder webClientBuilder, PatientsApiProperties properties) {
        this.webClient = webClientBuilder.baseUrl(properties.getBaseUrl()).build();
        this.properties = properties;
    }

    /**
     * Calls the patients API and returns the "content" array
     * as a List<Map<String, Object>>.
     */
    public List<Map<String, Object>> loadPatients(int page, int size) throws Exception {
        int effectiveSize = size > 0 ? size : properties.getDefaultPageSize();

        String uri = String.format(
                "%s?page=%d&size=%d",
                properties.getPath(),
                page,
                effectiveSize
        );

        Mono<String> responseMono = webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class);

        String body = responseMono.block();

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


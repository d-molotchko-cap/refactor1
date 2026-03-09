package com.acme.c8.jobworker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class PatientClient {

//    private static final ObjectMapper MAPPER = new ObjectMapper();
//    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Bean
    public HttpClient makeHttpClient() {
        return HttpClient.newHttpClient();
    }


    /**
     * Calls the patients API and returns the "content" array
     * as a List<Map<String, Object>>.
     */
    public static List<Map<String, Object>> loadPatients(int page, int size) throws Exception {

        //Start with a client which has the base URL https://api.capbpm.com/api
        //continue with appending Query Parameters with the http client methods
        String url = String.format(
                "https://api.capbpm.com/api/patients/load?page=%d&size=%d",
                page,
                size
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalStateException("Failed to load patients. HTTP " + response.statusCode());
        }

        // Parse full JSON response
        JsonNode root = MAPPER.readTree(response.body());

        // Extract "content" array
        JsonNode contentNode = root.get("content");
        if (contentNode == null || !contentNode.isArray()) {
            throw new IllegalStateException("Response does not contain a valid 'content' array");
        }

        // Convert to List<Map<String, Object>>
        return MAPPER.convertValue(
                contentNode,
                new TypeReference<>() {}
        );
    }

//    // Example usage
//    public static void main(String[] args) throws Exception {
//        List<Map<String, Object>> patients = loadPatients(0, 25);
//
//        log.info("Loaded patients: {}", patients.size());
//        log.info("First patient riskLevel: {}", patients.getFirst().get("riskLevel"));
//    }
}

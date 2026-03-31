package com.acme.c8.jobworker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/**
 * HTTP client for loading patient data from remote API.
 * Handles API communication and JSON parsing.
 */
@Slf4j
public class PatientClient {

    private static final String PATIENTS_API_URL = "https://api.capbpm.com/api/patients/load";
    private static final String CONTENT_FIELD = "content";
    private static final int HTTP_OK = 200;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private PatientClient() {
        // Utility class - no instantiation
    }

    /**
     * Load patients from the remote API.
     * Handles pagination through page and size parameters.
     *
     * @param page the page number to load
     * @param size the number of records per page
     * @return list of patient records
     * @throws Exception if API call fails or response is invalid
     */
    public static List<Map<String, Object>> loadPatients(int page, int size) throws Exception {
        String url = String.format("%s?page=%d&size=%d", PATIENTS_API_URL, page, size);
        log.debug("Loading patients from: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HTTP_OK) {
            log.error("Failed to load patients. HTTP status: {}", response.statusCode());
            throw new IllegalStateException("Failed to load patients. HTTP " + response.statusCode());
        }

        // Parse full JSON response
        JsonNode root = MAPPER.readTree(response.body());
        JsonNode contentNode = root.get(CONTENT_FIELD);

        if (contentNode == null || !contentNode.isArray()) {
            log.error("Response does not contain a valid '{}' array", CONTENT_FIELD);
            throw new IllegalStateException("Response does not contain a valid '" + CONTENT_FIELD + "' array");
        }

        // Convert to List<Map<String, Object>>
        List<Map<String, Object>> patients = MAPPER.convertValue(
                contentNode,
                new TypeReference<List<Map<String, Object>>>() {}
        );

        log.debug("Successfully loaded {} patients", patients.size());
        return patients;
    }
}

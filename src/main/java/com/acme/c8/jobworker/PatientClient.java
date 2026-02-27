package com.acme.c8.jobworker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class PatientClient {


    // Better to move to the properties
    public static final String URL = "https://api.capbpm.com/api/patients/load?page=%d&size=%d";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    /**
     * Calls the patients API and returns the "content" array
     * as a List<Map<String, Object>>.
     */
    public static List<Map<String, Object>> loadPatients(int page, int size) throws Exception {

        HttpResponse<String> response = processHttpRequest(page, size);

        return parseObject(response);
    }

    private static List<Map<String, Object>> parseObject(HttpResponse<String> response) throws JsonProcessingException {
        JsonNode root = MAPPER.readTree(response.body());

        // Extract "content" array
        JsonNode contentNode = root.get("content");
        if (contentNode == null || !contentNode.isArray()) {
            throw new IllegalStateException("Response does not contain a valid 'content' array");
        }

        // Convert to List<Map<String, Object>>
        return MAPPER.convertValue(
                contentNode,
                new TypeReference<>() {
                }
        );
    }

    private static @NonNull HttpResponse<String> processHttpRequest(int page, int size) throws IOException, InterruptedException {
        String url = String.format(
                URL,
                page,
                size
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "Failed to load patients. HTTP " + response.statusCode()
            );
        }

        return response;
    }

    // Example usage
    public static void main(String[] args) throws Exception {
        List<Map<String, Object>> patients = loadPatients(0, 25);

        System.out.println("Loaded patients: " + patients.size());
        System.out.println("First patient riskLevel: " + patients.getFirst().get("riskLevel"));
    }
}

package com.acme.c8;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.camunda.feel.api.FeelEngineApi;
import org.camunda.feel.api.FeelEngineBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for map operations and FEEL expression evaluation.
 * Provides type-safe access to map values and JSON serialization.
 */
@Slf4j
public class Util {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final FeelEngineApi FEEL_ENGINE = FeelEngineBuilder.forJava().build();

    private Util() {
        // Utility class - no instantiation
    }

    /**
     * Put a string value into a map if all parameters are non-null.
     */
    public static void setMapValue(Map<String, Object> map, Object key, Object value) {
        if (isValidForMapOperation(map, key, value)) {
            map.put(key.toString(), value.toString());
        }
    }

    /**
     * Put a value as-is into a map if all parameters are non-null.
     */
    public static void setMapValueAsObject(Map<String, Object> map, Object key, Object value) {
        if (isValidForMapOperation(map, key, value)) {
            map.put(key.toString(), value);
        }
    }

    /**
     * Serialize a map to pretty-printed JSON string.
     */
    public static String toPrettyJson(Map<String, Object> variables) throws JsonProcessingException {
        Objects.requireNonNull(variables, "Variables map cannot be null");
        return OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
                .writeValueAsString(variables);
    }

    /**
     * Parse a boolean value from a map, returning "false" as default.
     */
    public static String parseBoolean(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return "false";
        }

        Object value = map.get(key);
        if (value == null) {
            return "false";
        }

        if (value instanceof Boolean) {
            return value.toString();
        }

        return Boolean.parseBoolean(value.toString()) ? "true" : "false";
    }

    /**
     * Get a string value from a map, returning empty string if not found.
     */
    public static String getStringValue(Map<String, Object> map, String key) {
        return map != null && map.get(key) != null ? map.get(key).toString() : "";
    }

    /**
     * Get a map value from a map with type safety.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapValue(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return null;
        }

        Object value = map.get(key);
        return (value instanceof Map) ? (Map<String, Object>) value : null;
    }

    /**
     * Get a list value from a map with type safety.
     */
    @SuppressWarnings("unchecked")
    public static List<Object> getListValue(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return null;
        }

        Object value = map.get(key);
        return (value instanceof List) ? (List<Object>) value : null;
    }

    /**
     * Evaluate a FEEL expression and return the result as a JsonNode.
     *
     * @param feel      FEEL expression string
     * @param variables Input variables for the expression
     * @return JsonNode representing the result, or null if evaluation fails
     */
    public static JsonNode evaluateFeel(String feel, Map<String, Object> variables)
            throws JsonProcessingException {
        try {
            var result = FEEL_ENGINE.evaluateExpression(feel, variables);

            if (result == null || result.isFailure()) {
                log.warn("FEEL evaluation failed for expression: {}", feel);
                return null;
            }

            return OBJECT_MAPPER.readTree(
                    OBJECT_MAPPER.writeValueAsString(result.result())
            );
        } catch (Exception e) {
            log.error("Error evaluating FEEL expression: {}", feel, e);
            throw e;
        }
    }

    /**
     * Validate that map and parameters are suitable for map operations.
     */
    private static boolean isValidForMapOperation(Map<String, Object> map, Object key, Object value) {
        return map != null && key != null && value != null;
    }
}

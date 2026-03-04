package com.acme.c8;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.camunda.feel.api.FeelEngineApi;
import org.camunda.feel.api.FeelEngineBuilder;

import java.util.List;
import java.util.Map;

public class Util {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectMapper PRETTY_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    private static final FeelEngineApi FEEL_ENGINE = FeelEngineBuilder.forJava().build();

    public static void setMapValue(Map<String, Object> map, Object key, Object value) {
        if (map == null || key == null || value == null) return;
        map.put(key.toString(), value);
    }

    public static String toPrettyJson(Map<String, Object> variables) throws JsonProcessingException {
        return PRETTY_MAPPER.writeValueAsString(variables);
    }

    public static boolean parseBoolean(Map<String, Object> map, String key) {
        if (map == null || key == null) return false;
        Object value = map.get(key);
        if (value == null) return false;
        if (value instanceof Boolean b) return b;
        return Boolean.parseBoolean(value.toString());
    }

    public static String getStringValue(Map<String, Object> map, String key) {
        if (map == null) return "";
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    public static Map<String, Object> getMapValue(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object value = map.get(key);
        if (value instanceof Map<?, ?> m) return (Map<String, Object>) m;
        return null;
    }

    public static List<Object> getListValue(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object value = map.get(key);
        if (value instanceof List<?> l) return (List<Object>) l;
        return null;
    }

    public static JsonNode evaluateFeel(String feel, Map<String, Object> variables) throws JsonProcessingException {
        var result = FEEL_ENGINE.evaluateExpression(feel, variables);
        if (result == null || result.isFailure()) return null;
        return OBJECT_MAPPER.readTree(OBJECT_MAPPER.writeValueAsString(result.result()));
    }
}

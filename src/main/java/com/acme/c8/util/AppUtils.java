package com.acme.c8.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.camunda.feel.api.FeelEngineApi;
import org.camunda.feel.api.FeelEngineBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AppUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private AppUtils() {}

    public static void setMapValue(Map<String, Object> map, Object key, Object value) {
        if (map == null || key == null || value == null) return;
        map.put(key.toString(), value.toString());
    }

    public static void setMapValueAsObject(Map<String, Object> map, Object key, Object value) {
        if (map == null || key == null || value == null) return;
        map.put(key.toString(), value);
    }

    public static String toPrettyJson(HashMap<String, Object> variables) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsString(variables);
    }

    public static String parseBoolean(Map<String, Object> m, String key) {
        if (m == null || key == null) return "false";
        Object o = m.get(key);
        if (o instanceof Boolean) return o.toString();
        if (o != null && o.toString().equalsIgnoreCase("true")) return "true";
        return "false";
    }

    public static String getStringValue(Map<String, Object> m, String key) {
        Object o = m.get(key);
        return o != null ? o.toString() : "";
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapValue(Map<String, Object> m, String key) {
        Object o = m.get(key);
        return o instanceof Map ? (Map<String, Object>) o : null;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> getListValue(Map<String, Object> m, String key) {
        Object o = m.get(key);
        return o instanceof List ? (List<Object>) o : null;
    }

    public static JsonNode evaluateFeel(final String feel, final Map<String, Object> variables) throws JsonProcessingException {
        final FeelEngineApi feelEngineApi = FeelEngineBuilder.forJava().build();
        var result = feelEngineApi.evaluateExpression(feel, variables);
        if (result == null || result.isFailure()) return null;
        return OBJECT_MAPPER.readTree(OBJECT_MAPPER.writeValueAsString(result.result()));
    }
}

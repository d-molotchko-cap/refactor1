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
    private static final ObjectMapper PRETTY_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static void setMapValue(Map<String, Object> map, Object key, Object value) {
        if (map == null || key == null || value == null) return;
        map.put(key.toString(), value.toString());
    }

    public static void setMapValueAsObject(Map<String, Object> map, Object key, Object value) {
        if (map == null || key == null || value == null) return;
        map.put(key.toString(), value);
    }

    public static String toPrettyJson(Map<String, Object> variables) throws JsonProcessingException {
        return PRETTY_MAPPER.writeValueAsString(variables);
    }

    public static String parseBoolean(Map<String, Object> m, String key) {
        if (m == null || key == null) return "false";
        Object o = m.get(key);
        if (o instanceof Boolean b) return b.toString();
        if (o != null && o.toString().equalsIgnoreCase("true")) return "true";
        return "false";
    }

    public static String getStringValue(Map<String, Object> m, String key) {
        Object o = m.get(key);
        return o != null ? o.toString() : "";
    }

    public static Map<String, Object> getMapValue(Map<String, Object> m, String key) {
        return m.get(key) instanceof Map<?, ?> map ? (Map<String, Object>) map : null;
    }

    public static List<Object> getListValue(Map<String, Object> m, String key) {
        return m.get(key) instanceof List<?> list ? (List<Object>) list : null;
    }

    public static JsonNode evaluateFeel(String feel, Map<String, Object> variables) throws JsonProcessingException {
        FeelEngineApi feelEngineApi = FeelEngineBuilder.forJava().build();
        var result = feelEngineApi.evaluateExpression(feel, variables);

        if (result == null || result.isFailure()) return null;

        return OBJECT_MAPPER.readTree(OBJECT_MAPPER.writeValueAsString(result.result()));
    }
}

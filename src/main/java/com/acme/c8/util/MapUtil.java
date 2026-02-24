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

public class MapUtil {

    public static void setMapValue(Map<String, Object> map, Object key, Object value) {
        if (map == null || key == null || value == null) return;
        map.put(key.toString(), value.toString());
    }

    public static void setMapValueAsObject(Map<String, Object> map, Object key, Object value) {
        if (map == null || key == null || value == null) return;
        map.put(key.toString(), value);
    }

    public static String toPrettyJson(HashMap<String, Object> variables) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper.writeValueAsString(variables);
    }

    public static String parseBoolean(Map<String, Object> m, String key) {
        String retval = "false";
        try {
            if (m == null || key == null) {
                return retval;
            }
            Object o = m.get(key);
            if (o != null) {
                if (o instanceof Boolean) {
                    retval = o.toString();
                } else {
                    String s = o.toString();
                    if (s.equalsIgnoreCase("true")) {
                        retval = "true";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public static String getStringValue(Map<String, Object> m, String key) {
        Object o = m.get(key);
        return o != null ? o.toString() : "";
    }

    public static Map<String, Object> getMapValue(Map<String, Object> m, String key) {
        Object o = m.get(key);
        if (o instanceof Map) {
            return (Map<String, Object>) o;
        }
        return null;
    }

    public static List<Object> getListValue(Map<String, Object> m, String key) {
        Object o = m.get(key);
        if (o instanceof List) {
            return (List<Object>) o;
        }
        return null;
    }

    public static JsonNode evaluateFeel(final String feel, final Map<String, Object> variables) throws JsonProcessingException {
        final FeelEngineApi feelEngineApi = FeelEngineBuilder.forJava().build();
        var result = feelEngineApi.evaluateExpression(feel, variables);

        if (result == null || result.isFailure()) {
            return null;
        }

        final ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(objectMapper.writeValueAsString(result.result()));
    }
}

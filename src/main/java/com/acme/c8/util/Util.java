package com.acme.c8.util;

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
    private static final ObjectMapper PRETTY_OBJECT_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static void setMapValue(Map<String, Object> map, Object key, Object value) {
        if (map == null || key == null || value == null) return;

        String k = key.toString();
        String v = value.toString();

        map.put(k, v);
    }

    public static void setMapValueAsObject(Map<String, Object> map, Object key, Object value) {
        if (map == null || key == null || value == null) return;

        String k = key.toString();
        Object v = value;

        map.put(k, v);
    }

    public static String toPrettyJson(Map<String, Object> variables) throws JsonProcessingException {
        return PRETTY_OBJECT_MAPPER.writeValueAsString(variables);
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
        if (o != null) {
            return o.toString();
        } else {
            return "";
        }
    }

    public static Map<String, Object> getMapValue(Map<String, Object> m, String key) {
        Map<String, Object> retval = null;
        Object o = m.get(key);
        if (o != null && o instanceof Map) {
            retval = (Map<String, Object>) o;
        }
        return retval;
    }

    public static List<Object> getListValue(Map<String, Object> m, String key) {
        List<Object> retval = null;
        Object o = m.get(key);
        if (o != null && o instanceof List) {
            retval = (List<Object>) o;
        }
        return retval;
    }

    public static JsonNode evaluateFeel(final String feel, final Map<String, Object> variables) throws JsonProcessingException {
        final FeelEngineApi feelEngineApi = FeelEngineBuilder.forJava().build();
        var result = feelEngineApi.evaluateExpression(feel, variables);

        if (result == null || result.isFailure()) {
            return null;
        }

        return OBJECT_MAPPER.readTree(OBJECT_MAPPER.writeValueAsString(result.result()));
    }
}

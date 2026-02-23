package com.acme.c8.util;

import lombok.extern.slf4j.Slf4j;
import org.camunda.feel.api.FeelEngineApi;
import org.camunda.feel.api.FeelEngineBuilder;
import tools.jackson.core.JsonProcessingException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Util {

    private static final FeelEngineApi FEEL_ENGINE = FeelEngineBuilder.forJava().build();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

    public static String toPrettyJson(HashMap<String, Object> variables) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String prettyJson = objectMapper.writeValueAsString(variables);
        return prettyJson;
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
            log.warn("Failed to parse boolean for key '{}': {}", key, e.getMessage());
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
        var result = FEEL_ENGINE.evaluateExpression(feel, variables);

        if (result == null || result.isFailure()) {
            return null;
        }

        return OBJECT_MAPPER.readTree(OBJECT_MAPPER.writeValueAsString(result.result()));
    }
}

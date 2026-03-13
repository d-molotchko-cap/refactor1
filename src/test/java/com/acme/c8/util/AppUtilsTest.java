package com.acme.c8.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AppUtilsTest {

    @Test
    void evaluateFeel_filtersListByCondition() throws JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();
        List<Map<String, Object>> customerList = new ArrayList<>();
        customerList.add(Map.of("first", "Alice", "last", "Smith", "age", 8));
        customerList.add(Map.of("first", "Bob", "last", "Jones", "age", 18));
        customerList.add(Map.of("first", "Carol", "last", "White", "age", 28));
        customerList.add(Map.of("first", "Dave", "last", "Brown", "age", 7));
        customerList.add(Map.of("first", "Eve", "last", "Black", "age", 45));
        variables.put("customers", customerList);

        JsonNode result = AppUtils.evaluateFeel("customers[age>10]", variables);

        assertNotNull(result);
        assertTrue(result.isArray());
        assertEquals(3, result.size());
    }

    @Test
    void setMapValue_putsStringifiedValue() {
        Map<String, Object> map = new HashMap<>();
        AppUtils.setMapValue(map, "key", 42);
        assertEquals("42", map.get("key"));
    }

    @Test
    void setMapValue_doesNothingOnNullMap() {
        assertDoesNotThrow(() -> AppUtils.setMapValue(null, "key", "value"));
    }

    @Test
    void setMapValueAsObject_preservesOriginalType() {
        Map<String, Object> map = new HashMap<>();
        AppUtils.setMapValueAsObject(map, "num", 99);
        assertEquals(99, map.get("num"));
    }

    @Test
    void parseBoolean_returnsTrueForBooleanTrue() {
        Map<String, Object> map = Map.of("flag", true);
        assertEquals("true", AppUtils.parseBoolean(map, "flag"));
    }

    @Test
    void parseBoolean_returnsTrueForStringTrue() {
        Map<String, Object> map = Map.of("flag", "TRUE");
        assertEquals("true", AppUtils.parseBoolean(map, "flag"));
    }

    @Test
    void parseBoolean_returnsFalseForMissingKey() {
        Map<String, Object> map = new HashMap<>();
        assertEquals("false", AppUtils.parseBoolean(map, "missing"));
    }

    @Test
    void getStringValue_returnsEmptyStringWhenMissing() {
        Map<String, Object> map = new HashMap<>();
        assertEquals("", AppUtils.getStringValue(map, "missing"));
    }

    @Test
    void getStringValue_returnsStringRepresentation() {
        Map<String, Object> map = Map.of("key", 123);
        assertEquals("123", AppUtils.getStringValue(map, "key"));
    }

    @Test
    void getMapValue_returnsNestedMap() {
        Map<String, Object> nested = Map.of("inner", "value");
        Map<String, Object> map = new HashMap<>();
        map.put("nested", nested);

        Map<String, Object> result = AppUtils.getMapValue(map, "nested");
        assertNotNull(result);
        assertEquals("value", result.get("inner"));
    }

    @Test
    void getListValue_returnsList() {
        List<Object> list = List.of("a", "b", "c");
        Map<String, Object> map = new HashMap<>();
        map.put("items", list);

        List<Object> result = AppUtils.getListValue(map, "items");
        assertNotNull(result);
        assertEquals(3, result.size());
    }
}

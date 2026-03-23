package com.acme.c8;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Util class.
 * Tests FEEL expression evaluation and map operations.
 */
class UtilTest {

    @Test
    @DisplayName("Should evaluate FEEL expression with customer age filter")
    void testEvaluateFeelWithAgeFilter() throws JsonProcessingException {
        // Arrange
        Map<String, Object> variables = new HashMap<>();
        List<Map<String, Object>> customerList = new ArrayList<>();
        customerList.add(createCustomer("John", "Doe", 25));
        customerList.add(createCustomer("Jane", "Smith", 18));
        customerList.add(createCustomer("Bob", "Johnson", 30));
        customerList.add(createCustomer("Alice", "Brown", 16));
        customerList.add(createCustomer("Charlie", "Davis", 45));
        variables.put("customers", customerList);

        // Act
        JsonNode result = Util.evaluateFeel("customers[age>10]", variables);

        // Assert
        assertNotNull(result, "FEEL evaluation should not return null");
        assertTrue(result.isArray(), "Result should be an array");
    }

    /**
     * Create a customer map for testing.
     */
    private Map<String, Object> createCustomer(String firstName, String lastName, int age) {
        return Map.of(
                "first", firstName,
                "last", lastName,
                "age", age
        );
    }
}
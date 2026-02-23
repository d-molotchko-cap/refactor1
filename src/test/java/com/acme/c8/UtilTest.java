package com.acme.c8;

import com.acme.c8.util.Util;
import tools.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void evaluateFeel() throws JsonProcessingException {

        Map<String, Object> variables = new HashMap<>();
        List<Map<String, Object>> customerList = new ArrayList<>();
        customerList.add(createCustomer("first1", "last1", 8));
        customerList.add(createCustomer("first2", "last2", 18));
        customerList.add(createCustomer("first3", "last3", 28));
        customerList.add(createCustomer("first4", "last4", 7));
        customerList.add(createCustomer("first5", "last5", 45));
        variables.put("customers", customerList);

        var result = Util.evaluateFeel("customers[age>10]", variables);

        assertNotNull(result);
        System.out.println(result);
    }

    private Map<String, Object> createCustomer(String first, String last, int age) {
        return Map.of("first", first, "last", last, "age", age);
    }
}
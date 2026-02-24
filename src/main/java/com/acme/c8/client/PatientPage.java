package com.acme.c8.client;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PatientPage {
    private List<Map<String, Object>> content;
}

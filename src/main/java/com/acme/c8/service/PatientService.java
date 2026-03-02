package com.acme.c8.service;

import com.acme.c8.client.PatientClient;
import com.acme.c8.util.DmnAndFeelEvaluator;
import com.acme.c8.util.DmnEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientService {

    private final PatientClient patientClient;

    public Map<String, Object> findUser(String userId) {
        Map<String, Object> outputs = new HashMap<>();
        Boolean tmp = DmnAndFeelEvaluator.evaluateUserIsFound(userId);
        outputs.put("isFound", tmp);
        return outputs;
    }

    public long evaluatePatients(int pageIndex) throws Exception {
        List<Map<String, Object>> patientList = patientClient.loadPatients(pageIndex, 1000);
        return DmnEvaluator.go(patientList);
    }

    public static void main(String[] args) {
        System.out.println("DMN evaluator starting...");
    }
}

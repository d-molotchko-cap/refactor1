package com.acme.c8.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.acme.c8.jobworker.util.DmnAndFeelEvaluator;

@Service
public class JobWorkerService {

    public Map<String, Object> findUserImpl(  String userId) {
        Map<String, Object> outputs = new HashMap<>();
        Boolean tmp = DmnAndFeelEvaluator.evaluateUserIsFound(userId);
        outputs.put("isFound", tmp);
        return outputs;
    }

    public static void main(String[] args) {
        System.out.println("DMN evaluator starting...");
    }


}

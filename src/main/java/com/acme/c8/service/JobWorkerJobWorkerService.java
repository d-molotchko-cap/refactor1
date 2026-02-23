package com.acme.c8.service;

import com.acme.c8.util.DmnAndFeelEvaluator;
import com.acme.c8.util.DmnEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobWorkerJobWorkerService {

    private final DmnAndFeelEvaluator dmnAndFeelEvaluator;
    private final DmnEvaluator dmnEvaluator;

    public Map<String, Object> findUserImpl(String userId) {
        Map<String, Object> outputs = new HashMap<>();
        Boolean tmp = dmnAndFeelEvaluator.evaluateUserIsFound(userId);
        outputs.put("isFound", tmp);
        return outputs;
    }

    public Map<String, Object> filterPatients(int pageIndex) throws Exception {
        long duration = dmnEvaluator.go(pageIndex);
        Map<String, Object> outputs = new HashMap<>();
        outputs.put("duration", duration);
        return outputs;
    }
}

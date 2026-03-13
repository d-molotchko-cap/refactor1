package com.acme.c8.services;

import com.acme.c8.worker.dmn.DmnAndFeelEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientJobWorkerService {

    private final DmnAndFeelEvaluator dmnAndFeelEvaluator;

    public Map<String, Object> findUser(String userId) {
        log.debug("Evaluating user existence for userId: {}", userId);
        Map<String, Object> outputs = new HashMap<>();
        outputs.put("isFound", dmnAndFeelEvaluator.evaluateUserIsFound(userId));
        return outputs;
    }
}

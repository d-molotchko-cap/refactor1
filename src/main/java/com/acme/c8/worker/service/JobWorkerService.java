package com.acme.c8.worker.service;

import com.acme.c8.util.DmnAndFeelEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JobWorkerService {

    public Map<String, Object> findUserImpl(String userId) {
        Map<String, Object> outputs = new HashMap<>();
        boolean isFound = DmnAndFeelEvaluator.evaluateUserIsFound(userId);
        outputs.put("isFound", isFound);
        log.debug("User lookup completed. userId={}, isFound={}", userId, isFound);
        return outputs;
    }
}


package com.acme.c8.jobworker;

import com.acme.c8.jobworker.util.DmnAndFeelEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Service layer for job worker operations.
 * Encapsulates business logic for user and patient processing.
 */
@Component
@Slf4j
public class JobWorkerJobWorkerService {

    /**
     * Find a user by ID using DMN evaluation.
     *
     * @param userId the user ID to search for
     * @return map containing "isFound" boolean result
     */
    public Map<String, Object> findUserImpl(String userId) {
        log.debug("Finding user with ID: {}", userId);
        
        Map<String, Object> outputs = new HashMap<>();
        Boolean isFound = DmnAndFeelEvaluator.evaluateUserIsFound(userId);
        outputs.put("isFound", isFound);
        
        log.debug("User {} found: {}", userId, isFound);
        return outputs;
    }
}

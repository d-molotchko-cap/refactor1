package com.acme.c8.jobworker;
import java.util.*;

import com.acme.c8.jobworker.util.DmnAndFeelEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobWorkerJobWorkerService {

    public static Map<String, Object> findUserImpl(  String userId) {
        Map<String, Object> outputs = new HashMap<>();
        Boolean tmp = DmnAndFeelEvaluator.evaluateUserIsFound(userId);
        outputs.put("isFound", tmp);
        return outputs;
    }

    public static void main(String[] args) {
        log.info("DMN evaluator starting...");

        log.info("Find user with id 1. Result is {}", findUserImpl("1"));
        log.info("Find user with id 007. Result is {}", findUserImpl("007"));
    }
}

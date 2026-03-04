package com.acme.c8.jobworker;

import com.acme.c8.jobworker.util.DmnAndFeelEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CamundaJobWorkerService {

    public Map<String, Object> findUserImpl(String userId) {
        Map<String, Object> outputs = new HashMap<>();
        boolean isFound = DmnAndFeelEvaluator.evaluateUserIsFound(userId);
        outputs.put("isFound", isFound);
        return outputs;
    }
}

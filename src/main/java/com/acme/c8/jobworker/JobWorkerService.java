package com.acme.c8.jobworker;

import com.acme.c8.jobworker.util.DmnAndFeelEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobWorkerService {

    public boolean findUser(String userId) {
        return DmnAndFeelEvaluator.evaluateUserIsFound(userId);
    }

}

package com.acme.c8.service;

import com.acme.c8.dmn.DmnAndFeelEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class FindUserService {

    public Map<String, Object> findUserImpl(String userId) {
        Map<String, Object> outputs = new HashMap<>();
        Boolean tmp = DmnAndFeelEvaluator.evaluateUserIsFound(userId);
        outputs.put("isFound", tmp);
        return outputs;
    }
}

package com.acme.c8.worker;

import com.acme.c8.exception.InternalServerException;
import com.acme.c8.exception.WorkerException;
import com.acme.c8.service.FindUserService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class FindUserWorker {

    private final FindUserService service;

    @JobWorker(type = "${app.worker.find-user-type}", fetchVariables = {"userId"})
    public Map<String, Object> findUser(final ActivatedJob job, @Variable String userId) {
        final String METHOD_NAME = "FindUserWorker.findUser";
        Map<String, Object> inputVarMap = job.getVariablesAsMap();
        log.trace("{} started...", METHOD_NAME);

        try {
            Map<String, Object> outputs = service.findUserImpl(userId);
            log.trace("{} finished.", METHOD_NAME);
            return outputs;
        } catch (WorkerException e) {
            log.error("{} domain error [code={}]: {}", METHOD_NAME, e.getErrorCode(), e.getMessage());
            throw new InternalServerException(e.getMessage());
        } catch (Exception e) {
            log.error("{} unexpected error: {}", METHOD_NAME, e.getMessage());
            throw new InternalServerException(e.getMessage());
        }
    }
}

package com.acme.c8.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "patient-client",
        url = "${app.patient-client.base-url}",
        configuration = PatientClientConfig.class
)
public interface PatientClient {

    @GetMapping("/load")
    PatientPage loadPatients(@RequestParam("page") int page, @RequestParam("size") int size);
}

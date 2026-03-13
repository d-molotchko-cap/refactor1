package com.acme.c8.client;

import com.acme.c8.model.PatientPage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "patient-api", url = "${patient.api.url:https://api.capbpm.com}")
public interface PatientFeignClient {

    @GetMapping("/api/patients/load")
    PatientPage loadPatients(@RequestParam("page") int page, @RequestParam("size") int size);
}

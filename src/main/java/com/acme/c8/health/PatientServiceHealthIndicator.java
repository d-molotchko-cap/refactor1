package com.acme.c8.health;

import com.acme.c8.client.PatientFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PatientServiceHealthIndicator implements HealthIndicator {

    private final PatientFeignClient patientFeignClient;

    @Override
    public Health health() {
        try {
            patientFeignClient.loadPatients(0, 1);
            return Health.up().withDetail("patientApi", "Available").build();
        } catch (Exception e) {
            log.warn("Patient API health check failed: {}", e.getMessage());
            return Health.down().withDetail("patientApi", "Unavailable").withException(e).build();
        }
    }
}

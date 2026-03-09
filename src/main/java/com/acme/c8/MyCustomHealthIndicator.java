package com.acme.c8;

import com.acme.c8.jobworker.PatientClient;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MyCustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Custom logic to check health, e.g., external API or DB check
        boolean serviceRunning = checkMyService();

        if (serviceRunning) {
            return Health.up().withDetail("PatientService", "Available").build();
        } else {
            return Health.down().withDetail("PatientService", "Not Available").build();
        }
    }

    private boolean checkMyService() {
        try {
            PatientClient.loadPatients(0, 1);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

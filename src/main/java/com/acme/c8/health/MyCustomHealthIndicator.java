package com.acme.c8.health;


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
            return Health.up().withDetail("service", "Available").build();
        } else {
            return Health.down().withDetail("service", "Not Available").build();
        }
    }

    private boolean checkMyService() {
        // Simulate some logic to determine if the service is running
        return true;
    }
}

package com.acme.c8.controller;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootHealthController {

    private final HealthEndpoint healthEndpoint;

    public RootHealthController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping("/")
    public Health rootHealth() {
        return healthEndpoint.health();
    }
}


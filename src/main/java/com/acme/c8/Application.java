package com.acme.c8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Camunda 8 Job Worker application.
 * 
 * This Spring Boot application integrates with Camunda BPM 8 platform
 * to process job tasks, including patient data analysis and user lookups
 * using DMN (Decision Model and Notation) decisions.
 * 
 * Features:
 * - Job Worker for processing Camunda jobs
 * - DMN and FEEL expression evaluation
 * - Patient risk assessment
 * - Actuator health checks
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

package com.acme.c8.config;

import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DmnEngineConfig {

    @Bean
    public DmnEngine dmnEngine() {
        return DefaultDmnEngineConfiguration
                .createDefaultDmnEngineConfiguration()
                .buildEngine();
    }
}

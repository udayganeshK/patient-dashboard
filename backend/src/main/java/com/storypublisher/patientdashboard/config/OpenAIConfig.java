package com.storypublisher.patientdashboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIConfig {
    @Value("${openai.api.key}")
    private String openAIApiKey;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getOpenAIApiKey() {
        return openAIApiKey;
    }
}

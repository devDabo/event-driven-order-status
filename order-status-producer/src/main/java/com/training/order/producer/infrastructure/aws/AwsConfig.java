package com.training.order.producer.infrastructure.aws;

import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String region;

    @Bean
    public SecretsManagerClient secretsManager(){
        returns Secrets
    }
}

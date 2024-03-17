package com.akgarg.subsservice.security;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

@Configuration
public class SecurityBeanConfig {

    @LoadBalanced
    @Bean
    @Profile("dev")
    public RestClient restClient() {
        return RestClient.builder().build();
    }

}

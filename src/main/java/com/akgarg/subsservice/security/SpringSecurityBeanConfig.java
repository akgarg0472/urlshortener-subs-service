package com.akgarg.subsservice.security;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class SpringSecurityBeanConfig {

    @LoadBalanced
    @Bean
    public RestClient.Builder authServiceRestClientBuilder() {
        return RestClient.builder()
                .baseUrl("http://urlshortener-subscription-service");
    }

}

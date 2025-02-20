package com.akgarg.subsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SubscriptionServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SubscriptionServiceApplication.class, args);
    }

}

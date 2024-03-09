package com.akgarg.subsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SubsServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SubsServiceApplication.class, args);
    }

}

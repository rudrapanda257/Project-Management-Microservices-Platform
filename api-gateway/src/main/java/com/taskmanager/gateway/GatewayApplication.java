package com.taskmanager.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway Application
 * Routes incoming requests to appropriate microservices
 * Handles JWT authentication at the gateway level
 * Integrates with Eureka for service discovery
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("====================================");
        System.out.println("API Gateway is running on port 8090");
        System.out.println("All requests should go through gateway");
        System.out.println("====================================");
    }
}
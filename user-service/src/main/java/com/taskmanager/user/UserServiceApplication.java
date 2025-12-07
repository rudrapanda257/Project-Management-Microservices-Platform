package com.taskmanager.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * User Service Application
 * 
 * Handles user authentication, registration, and JWT token management.
 * Registers with Eureka Server for service discovery.
 * 
 * Port: 8081
 * Eureka: http://localhost:8761
 */
@SpringBootApplication
@EnableDiscoveryClient // NEW - Enable service registration with Eureka
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        System.out.println("=================================================");
        System.out.println("   User Service Started Successfully!");
        System.out.println("   Port: 8081");
        System.out.println("   Registering with Eureka Server...");
        System.out.println("=================================================");
    }
}
package com.taskmanager.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Project Service Application
 * 
 * Handles project and task management operations.
 * Registers with Eureka Server for service discovery.
 * 
 * Port: 8082
 * Eureka: http://localhost:8761
 */
@SpringBootApplication
@EnableDiscoveryClient // NEW - Enable service registration with Eureka
@EnableFeignClients
public class ProjectServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectServiceApplication.class, args);
        System.out.println("=================================================");
        System.out.println("   Project Service Started Successfully!");
        System.out.println("   Port: 8082");
        System.out.println("   Registering with Eureka Server...");
        System.out.println("=================================================");
    }
}
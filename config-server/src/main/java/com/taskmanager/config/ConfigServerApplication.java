package com.taskmanager.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config Server Application
 * Provides centralized configuration management for all microservices
 * Configurations are stored in a Git repository
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
        System.out.println("====================================");
        System.out.println("Config Server is running on port 8888");
        System.out.println("====================================");
    }
}
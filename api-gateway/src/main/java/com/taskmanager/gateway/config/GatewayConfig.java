package com.taskmanager.gateway.config;

import com.taskmanager.gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway Configuration
 * Defines routes and applies JWT filter
 */
@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

//     @Bean
//     public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//         return builder.routes()
//                 // User Service Routes
//                 .route("user-service", r -> r
//                         .path("/api/auth/**")
//                         .filters(f -> f.filter(jwtAuthenticationFilter))
//                         .uri("lb://USER-SERVICE"))
                
//                 // Project Service - Projects Routes
//                 .route("project-service-projects", r -> r
//                         .path("/api/projects/**")
//                         .filters(f -> f.filter(jwtAuthenticationFilter))
//                         .uri("lb://PROJECT-SERVICE"))
                
//                 // Project Service - Tasks Routes
//                 .route("project-service-tasks", r -> r
//                         .path("/api/tasks/**")
//                         .filters(f -> f.filter(jwtAuthenticationFilter))
//                         .uri("lb://PROJECT-SERVICE"))
                
//                 .build();
//     }
}
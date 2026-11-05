package com.audible.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("content-service", r -> r
                        .path("/api/v1/content/**")
                        .uri("http://content-service:8001"))
                .route("library-service", r -> r
                        .path("/api/v1/library/**")
                        .uri("http://library-service:8002"))
                .route("notification-service", r -> r
                        .path("/api/v1/notifications/**")
                        .uri("http://notification-service:8003"))
                .build();
    }
}

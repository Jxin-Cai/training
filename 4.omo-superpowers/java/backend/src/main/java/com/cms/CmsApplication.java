package com.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CMS Application Entry Point
 * 
 * This application follows Hexagonal DDD Architecture:
 * - domain: Core business logic and entities
 * - application: Use case services and DTOs
 * - infrastructure: Repository implementations and configurations
 * - presentation: REST controllers
 */
@SpringBootApplication
public class CmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmsApplication.class, args);
    }
}

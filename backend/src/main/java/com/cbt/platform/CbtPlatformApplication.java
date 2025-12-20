package com.cbt.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for CBT Platform
 * Config-driven platform for CBT courses
 */
@SpringBootApplication
@EnableJpaAuditing
public class CbtPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(CbtPlatformApplication.class, args);
    }
}

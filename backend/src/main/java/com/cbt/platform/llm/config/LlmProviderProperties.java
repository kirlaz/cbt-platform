package com.cbt.platform.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for LLM providers
 * Binds to llm.* properties in application.yml
 */
@Component
@ConfigurationProperties(prefix = "llm")
@Data
public class LlmProviderProperties {

    /**
     * Default provider to use
     */
    private ProviderType defaultProvider = ProviderType.CLAUDE;

    /**
     * Retry configuration
     */
    private RetryConfig retry = new RetryConfig();

    /**
     * Per-provider configurations
     */
    private Map<String, ProviderConfig> providers = new HashMap<>();

    @Data
    public static class RetryConfig {
        private int maxAttempts = 3;
        private long backoffDelay = 1000; // milliseconds
    }

    @Data
    public static class ProviderConfig {
        private boolean enabled = false;
        private String apiKey;
        private String baseUrl;
        private String model;
        private Integer maxTokens = 1024;
        private Double temperature = 0.7;
        private Long timeout = 30000L; // milliseconds
    }

    /**
     * Get configuration for specific provider
     */
    public ProviderConfig getProviderConfig(ProviderType type) {
        return providers.get(type.name().toLowerCase());
    }

    /**
     * Check if provider is enabled
     */
    public boolean isProviderEnabled(ProviderType type) {
        ProviderConfig config = getProviderConfig(type);
        return config != null && config.isEnabled();
    }
}

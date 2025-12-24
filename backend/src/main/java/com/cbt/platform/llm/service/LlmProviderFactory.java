package com.cbt.platform.llm.service;

import com.cbt.platform.llm.config.LlmProviderProperties;
import com.cbt.platform.llm.config.ProviderType;
import com.cbt.platform.llm.exception.LlmProviderException;
import com.cbt.platform.llm.provider.LlmProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory for LLM providers (Strategy pattern)
 * Auto-discovers and registers all available LlmProvider beans
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LlmProviderFactory {

    private final List<LlmProvider> providers;
    private final LlmProviderProperties properties;
    private final Map<ProviderType, LlmProvider> providerMap = new HashMap<>();

    /**
     * Register all providers after bean construction
     * Spring auto-injects all LlmProvider beans via constructor
     */
    @PostConstruct
    public void registerProviders() {
        log.info("Registering LLM providers...");

        for (LlmProvider provider : providers) {
            ProviderType type = provider.getProviderType();

            if (provider.isConfigured()) {
                providerMap.put(type, provider);
                log.info("Registered LLM provider: {} (model: {})", type, provider.getModel());
            } else {
                log.warn("Provider {} is not configured, skipping", type);
            }
        }

        log.info("Registered {} LLM providers", providerMap.size());

        if (providerMap.isEmpty()) {
            log.warn("No LLM providers are configured! LLM features will not work.");
        }
    }

    /**
     * Get provider by type
     *
     * @param type Provider type
     * @return LlmProvider instance
     * @throws LlmProviderException if provider not found or not configured
     */
    public LlmProvider getProvider(ProviderType type) {
        LlmProvider provider = providerMap.get(type);
        if (provider == null) {
            throw new LlmProviderException(
                    "Provider not available: " + type + ". Check configuration."
            );
        }
        return provider;
    }

    /**
     * Get default provider (from configuration)
     *
     * @return Default LlmProvider instance
     * @throws LlmProviderException if default provider not available
     */
    public LlmProvider getDefaultProvider() {
        ProviderType defaultType = properties.getDefaultProvider();
        return getProvider(defaultType);
    }

    /**
     * Check if provider is available
     *
     * @param type Provider type
     * @return true if provider is registered and configured
     */
    public boolean isProviderAvailable(ProviderType type) {
        return providerMap.containsKey(type);
    }

    /**
     * Get all available provider types
     *
     * @return List of available provider types
     */
    public List<ProviderType> getAvailableProviders() {
        return List.copyOf(providerMap.keySet());
    }
}

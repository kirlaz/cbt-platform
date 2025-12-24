package com.cbt.platform.llm.provider;

import com.cbt.platform.llm.config.LlmProviderProperties;
import com.cbt.platform.llm.exception.LlmProviderException;
import com.cbt.platform.llm.exception.RateLimitException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.function.Supplier;

/**
 * Abstract base class for LLM providers
 * Provides common functionality: retry logic, error handling, config access
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractLlmProvider implements LlmProvider {

    protected final RestClient restClient;
    protected final ObjectMapper objectMapper;
    protected final LlmProviderProperties properties;

    /**
     * Execute operation with retry logic
     */
    protected <T> T executeWithRetry(Supplier<T> operation) {
        int maxAttempts = properties.getRetry().getMaxAttempts();
        long backoffDelay = properties.getRetry().getBackoffDelay();

        Exception lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return operation.get();
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                lastException = e;
                handleHttpError(e.getStatusCode(), e.getResponseBodyAsString());

                if (e.getStatusCode().value() == 429) {
                    // Rate limit - retry with backoff
                    if (attempt < maxAttempts) {
                        sleep(backoffDelay * attempt);
                        log.warn("Rate limited, retrying attempt {}/{}", attempt + 1, maxAttempts);
                        continue;
                    }
                }

                // Other client errors - don't retry
                if (e.getStatusCode().is4xxClientError()) {
                    throw new LlmProviderException("Client error: " + e.getMessage(), e);
                }

                // Server errors - retry
                if (attempt < maxAttempts) {
                    sleep(backoffDelay * attempt);
                    log.warn("Server error, retrying attempt {}/{}", attempt + 1, maxAttempts);
                    continue;
                }

                throw new LlmProviderException("Server error after " + maxAttempts + " attempts", e);

            } catch (Exception e) {
                lastException = e;
                log.error("Unexpected error on attempt {}/{}", attempt, maxAttempts, e);

                if (attempt < maxAttempts) {
                    sleep(backoffDelay * attempt);
                    continue;
                }

                throw new LlmProviderException("Operation failed after " + maxAttempts + " attempts", e);
            }
        }

        throw new LlmProviderException("All retry attempts failed", lastException);
    }

    /**
     * Handle HTTP errors
     */
    protected void handleHttpError(HttpStatusCode status, String body) {
        log.error("HTTP error: status={}, body={}", status, body);

        if (status.value() == 429) {
            throw new RateLimitException();
        }

        if (status.is4xxClientError()) {
            throw new LlmProviderException(
                    "Client error: " + status + " - " + body,
                    org.springframework.http.HttpStatus.valueOf(status.value())
            );
        }

        if (status.is5xxServerError()) {
            throw new LlmProviderException(
                    "Server error: " + status + " - " + body,
                    org.springframework.http.HttpStatus.valueOf(status.value())
            );
        }
    }

    /**
     * Sleep for exponential backoff
     */
    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LlmProviderException("Interrupted during backoff", e);
        }
    }

    /**
     * Get provider configuration
     */
    protected LlmProviderProperties.ProviderConfig getConfig() {
        return properties.getProviderConfig(getProviderType());
    }

    @Override
    public boolean isConfigured() {
        LlmProviderProperties.ProviderConfig config = getConfig();
        return config != null &&
                config.isEnabled() &&
                config.getApiKey() != null &&
                !config.getApiKey().isEmpty();
    }

    @Override
    public String getModel() {
        LlmProviderProperties.ProviderConfig config = getConfig();
        return config != null ? config.getModel() : "unknown";
    }
}

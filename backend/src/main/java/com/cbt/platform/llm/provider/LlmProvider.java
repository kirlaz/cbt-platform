package com.cbt.platform.llm.provider;

import com.cbt.platform.llm.config.ProviderType;
import com.cbt.platform.llm.dto.LlmRequest;
import com.cbt.platform.llm.dto.LlmResponse;

/**
 * LLM Provider interface (Strategy pattern)
 * Each provider (Claude, OpenAI, Gemini, Local) implements this interface
 */
public interface LlmProvider {

    /**
     * Get provider type
     */
    ProviderType getProviderType();

    /**
     * Send message and get response (blocking)
     *
     * @param request LLM request
     * @return LLM response
     */
    LlmResponse sendMessage(LlmRequest request);

    /**
     * Check if provider supports streaming
     *
     * @return true if streaming is supported
     */
    boolean supportsStreaming();

    /**
     * Check if provider is properly configured
     *
     * @return true if API key and config are valid
     */
    boolean isConfigured();

    /**
     * Get model name being used
     *
     * @return model identifier
     */
    String getModel();
}

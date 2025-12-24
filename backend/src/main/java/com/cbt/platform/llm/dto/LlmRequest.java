package com.cbt.platform.llm.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * Unified LLM request DTO (provider-agnostic)
 */
@Builder
public record LlmRequest(
        /**
         * System prompt (sets AI behavior and context)
         */
        String systemPrompt,

        /**
         * Conversation messages (user/assistant history)
         */
        List<LlmMessage> messages,

        /**
         * User data for template resolution
         */
        JsonNode userData,

        /**
         * Provider-specific parameters (temperature, maxTokens, etc.)
         */
        Map<String, Object> parameters,

        /**
         * Conversation ID for context management (optional)
         */
        String conversationId,

        /**
         * Enable streaming response (SSE)
         */
        boolean stream
) {
    public LlmRequest {
        // Default empty parameters if null
        if (parameters == null) {
            parameters = Map.of();
        }
    }

    /**
     * Get parameter value with default
     */
    public <T> T getParameter(String key, T defaultValue) {
        Object value = parameters.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            @SuppressWarnings("unchecked")
            T result = (T) value;
            return result;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    public Double getTemperature() {
        return getParameter("temperature", 0.7);
    }

    public Integer getMaxTokens() {
        return getParameter("maxTokens", 1024);
    }
}

package com.cbt.platform.llm.dto;

import lombok.Builder;

import java.util.Map;

/**
 * Unified LLM response DTO (provider-agnostic)
 */
@Builder
public record LlmResponse(
        /**
         * Generated text content
         */
        String content,

        /**
         * Reason for completion (stop, length, etc.)
         */
        String finishReason,

        /**
         * Total tokens used (input + output)
         */
        Integer tokensUsed,

        /**
         * Model that generated the response
         */
        String model,

        /**
         * Provider-specific metadata
         */
        Map<String, Object> metadata
) {
}

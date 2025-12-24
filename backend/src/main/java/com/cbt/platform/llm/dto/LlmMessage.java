package com.cbt.platform.llm.dto;

/**
 * Single message in LLM conversation
 *
 * @param role    Message role: "system", "user", or "assistant"
 * @param content Message text content
 */
public record LlmMessage(
        String role,
        String content
) {
    public static LlmMessage system(String content) {
        return new LlmMessage("system", content);
    }

    public static LlmMessage user(String content) {
        return new LlmMessage("user", content);
    }

    public static LlmMessage assistant(String content) {
        return new LlmMessage("assistant", content);
    }
}

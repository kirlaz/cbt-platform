package com.cbt.platform.llm.config;

/**
 * Supported LLM provider types
 */
public enum ProviderType {
    /**
     * Anthropic Claude (claude-3-5-sonnet, claude-3-opus, etc.)
     */
    CLAUDE,

    /**
     * OpenAI (GPT-4, GPT-4o, GPT-3.5-turbo, etc.)
     */
    OPENAI,

    /**
     * Google Gemini (gemini-1.5-pro, etc.)
     */
    GEMINI,

    /**
     * Local LLM via Ollama (llama3, mistral, etc.)
     */
    LOCAL
}

package com.cbt.platform.llm.service;

import com.cbt.platform.llm.config.ProviderType;
import com.cbt.platform.llm.dto.LlmMessage;
import com.cbt.platform.llm.dto.LlmRequest;
import com.cbt.platform.llm.dto.LlmResponse;
import com.cbt.platform.llm.provider.LlmProvider;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Main LLM service facade
 * Provides simple interface for sending messages to LLM providers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LlmService {

    private final LlmProviderFactory providerFactory;
    private final PromptBuilder promptBuilder;

    /**
     * Send message using default provider
     *
     * @param systemPrompt System prompt (defines AI behavior)
     * @param userMessage  User message
     * @param userData     User data for template resolution
     * @return LLM response
     */
    public LlmResponse sendMessage(
            String systemPrompt,
            String userMessage,
            JsonNode userData
    ) {
        return sendMessage(systemPrompt, userMessage, userData, Map.of());
    }

    /**
     * Send message with parameters
     *
     * @param systemPrompt System prompt
     * @param userMessage  User message
     * @param userData     User data
     * @param parameters   Additional parameters (temperature, maxTokens, etc.)
     * @return LLM response
     */
    public LlmResponse sendMessage(
            String systemPrompt,
            String userMessage,
            JsonNode userData,
            Map<String, Object> parameters
    ) {
        LlmProvider provider = providerFactory.getDefaultProvider();
        log.debug("Sending message using provider: {}", provider.getProviderType());

        // Resolve templates in system prompt
        String resolvedSystemPrompt = promptBuilder.buildPrompt(systemPrompt, userData);

        // Resolve templates in user message
        String resolvedUserMessage = promptBuilder.buildPrompt(userMessage, userData);

        LlmRequest request = LlmRequest.builder()
                .systemPrompt(resolvedSystemPrompt)
                .messages(List.of(LlmMessage.user(resolvedUserMessage)))
                .userData(userData)
                .parameters(parameters)
                .stream(false)
                .build();

        return provider.sendMessage(request);
    }

    /**
     * Send message with conversation history
     *
     * @param systemPrompt System prompt
     * @param messages     Conversation messages (user/assistant history)
     * @param userData     User data
     * @return LLM response
     */
    public LlmResponse sendConversation(
            String systemPrompt,
            List<LlmMessage> messages,
            JsonNode userData
    ) {
        return sendConversation(systemPrompt, messages, userData, Map.of());
    }

    /**
     * Send message with conversation history and parameters
     *
     * @param systemPrompt System prompt
     * @param messages     Conversation messages
     * @param userData     User data
     * @param parameters   Additional parameters
     * @return LLM response
     */
    public LlmResponse sendConversation(
            String systemPrompt,
            List<LlmMessage> messages,
            JsonNode userData,
            Map<String, Object> parameters
    ) {
        LlmProvider provider = providerFactory.getDefaultProvider();

        String resolvedSystemPrompt = promptBuilder.buildPrompt(systemPrompt, userData);

        LlmRequest request = LlmRequest.builder()
                .systemPrompt(resolvedSystemPrompt)
                .messages(messages)
                .userData(userData)
                .parameters(parameters)
                .stream(false)
                .build();

        return provider.sendMessage(request);
    }

    /**
     * Generate response using specific provider
     *
     * @param providerType Provider type
     * @param systemPrompt System prompt
     * @param userMessage  User message
     * @param userData     User data
     * @return LLM response
     */
    public LlmResponse sendWithProvider(
            ProviderType providerType,
            String systemPrompt,
            String userMessage,
            JsonNode userData
    ) {
        LlmProvider provider = providerFactory.getProvider(providerType);
        log.debug("Sending message using specified provider: {}", providerType);

        String resolvedSystemPrompt = promptBuilder.buildPrompt(systemPrompt, userData);
        String resolvedUserMessage = promptBuilder.buildPrompt(userMessage, userData);

        LlmRequest request = LlmRequest.builder()
                .systemPrompt(resolvedSystemPrompt)
                .messages(List.of(LlmMessage.user(resolvedUserMessage)))
                .userData(userData)
                .stream(false)
                .build();

        return provider.sendMessage(request);
    }

    /**
     * Check if any LLM provider is available
     *
     * @return true if at least one provider is configured
     */
    public boolean isAvailable() {
        return !providerFactory.getAvailableProviders().isEmpty();
    }

    /**
     * Check if specific provider is available
     *
     * @param type Provider type
     * @return true if provider is available
     */
    public boolean isProviderAvailable(ProviderType type) {
        return providerFactory.isProviderAvailable(type);
    }
}

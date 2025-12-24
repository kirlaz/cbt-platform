package com.cbt.platform.llm.provider.claude;

import com.cbt.platform.llm.config.LlmProviderProperties;
import com.cbt.platform.llm.config.ProviderType;
import com.cbt.platform.llm.dto.LlmMessage;
import com.cbt.platform.llm.dto.LlmRequest;
import com.cbt.platform.llm.dto.LlmResponse;
import com.cbt.platform.llm.exception.LlmProviderException;
import com.cbt.platform.llm.provider.AbstractLlmProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Claude (Anthropic) LLM Provider
 * API Docs: https://docs.anthropic.com/claude/reference/messages_post
 */
@Component
@ConditionalOnProperty(
        prefix = "llm.providers.claude",
        name = "enabled",
        havingValue = "true"
)
@Slf4j
public class ClaudeProvider extends AbstractLlmProvider {

    public ClaudeProvider(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            LlmProviderProperties properties
    ) {
        super(
                restClientBuilder
                        .baseUrl(properties.getProviderConfig(ProviderType.CLAUDE).getBaseUrl())
                        .build(),
                objectMapper,
                properties
        );
    }

    @Override
    public ProviderType getProviderType() {
        return ProviderType.CLAUDE;
    }

    @Override
    public LlmResponse sendMessage(LlmRequest request) {
        log.debug("Sending message to Claude: systemPrompt={}, messages={}",
                request.systemPrompt(), request.messages().size());

        return executeWithRetry(() -> {
            // Build Claude API request
            Map<String, Object> claudeRequest = buildClaudeRequest(request);

            // Call Claude API
            Map<String, Object> response = restClient.post()
                    .uri("/v1/messages")
                    .header("x-api-key", getConfig().getApiKey())
                    .header("anthropic-version", "2023-06-01")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(claudeRequest)
                    .retrieve()
                    .body(Map.class);

            // Convert to unified response
            return convertToLlmResponse(response);
        });
    }

    @Override
    public boolean supportsStreaming() {
        return true; // Claude supports SSE streaming
    }

    /**
     * Build Claude API request from unified LlmRequest
     */
    private Map<String, Object> buildClaudeRequest(LlmRequest request) {
        Map<String, Object> claudeRequest = new HashMap<>();

        // Model
        claudeRequest.put("model", getConfig().getModel());

        // Max tokens
        claudeRequest.put("max_tokens", request.getMaxTokens());

        // Temperature
        claudeRequest.put("temperature", request.getTemperature());

        // System prompt
        if (request.systemPrompt() != null && !request.systemPrompt().isEmpty()) {
            claudeRequest.put("system", request.systemPrompt());
        }

        // Messages
        List<Map<String, String>> messages = request.messages().stream()
                .filter(msg -> !msg.role().equals("system")) // System is separate in Claude
                .map(msg -> Map.of(
                        "role", msg.role(),
                        "content", msg.content()
                ))
                .collect(Collectors.toList());

        claudeRequest.put("messages", messages);

        return claudeRequest;
    }

    /**
     * Convert Claude API response to unified LlmResponse
     */
    @SuppressWarnings("unchecked")
    private LlmResponse convertToLlmResponse(Map<String, Object> response) {
        if (response == null) {
            throw new LlmProviderException("Null response from Claude API");
        }

        // Extract content
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        String textContent = "";
        if (content != null && !content.isEmpty()) {
            textContent = (String) content.get(0).get("text");
        }

        // Extract usage
        Map<String, Object> usage = (Map<String, Object>) response.get("usage");
        int inputTokens = usage != null ? (Integer) usage.get("input_tokens") : 0;
        int outputTokens = usage != null ? (Integer) usage.get("output_tokens") : 0;

        return LlmResponse.builder()
                .content(textContent)
                .finishReason((String) response.get("stop_reason"))
                .tokensUsed(inputTokens + outputTokens)
                .model((String) response.get("model"))
                .metadata(Map.of(
                        "id", response.get("id"),
                        "type", response.get("type"),
                        "inputTokens", inputTokens,
                        "outputTokens", outputTokens
                ))
                .build();
    }
}

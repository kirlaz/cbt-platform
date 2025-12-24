package com.cbt.platform.engine.handler;

import com.cbt.platform.engine.dto.BlockResult;
import com.cbt.platform.engine.dto.BlockType;
import com.cbt.platform.llm.dto.LlmResponse;
import com.cbt.platform.llm.service.LlmService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handler for LLM_RESPONSE blocks
 * Generates response based on user data (one-time generation, not chat)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LlmResponseBlockHandler implements BlockHandler {

    private final LlmService llmService;
    private final ObjectMapper objectMapper;

    @Override
    public BlockType getBlockType() {
        return BlockType.LLM_RESPONSE;
    }

    @Override
    public BlockResult handle(JsonNode blockData, JsonNode userData, JsonNode userInput) {
        String blockId = blockData.get("id").asText();
        log.debug("Processing LLM_RESPONSE block: {}", blockId);

        if (!llmService.isAvailable()) {
            log.error("LLM service is not available");
            return BlockResult.builder()
                    .blockId(blockId)
                    .blockType(BlockType.LLM_RESPONSE)
                    .content(blockData)
                    .requiresInput(false)
                    .isComplete(true)
                    .error("LLM service is not configured")
                    .updatedUserData(userData)
                    .build();
        }

        try {
            // Get prompts from block config
            String systemPrompt = blockData.has("system_prompt") ?
                    blockData.get("system_prompt").asText() : "";
            String userPrompt = blockData.has("prompt") ?
                    blockData.get("prompt").asText() : "";

            // Generate response using LLM
            LlmResponse llmResponse = llmService.sendMessage(
                    systemPrompt,
                    userPrompt,
                    userData
            );

            // Create content with generated response
            ObjectNode content = objectMapper.createObjectNode();
            content.put("type", "llm_response");
            content.put("response", llmResponse.content());
            content.put("model", llmResponse.model());

            log.debug("Generated LLM response: {} tokens", llmResponse.tokensUsed());

            return BlockResult.builder()
                    .blockId(blockId)
                    .blockType(BlockType.LLM_RESPONSE)
                    .content(content)
                    .requiresInput(false)
                    .isComplete(true)
                    .updatedUserData(userData)
                    .build();

        } catch (Exception e) {
            log.error("Error generating LLM response", e);
            return BlockResult.builder()
                    .blockId(blockId)
                    .blockType(BlockType.LLM_RESPONSE)
                    .content(blockData)
                    .requiresInput(false)
                    .isComplete(true)
                    .error("Failed to generate response: " + e.getMessage())
                    .updatedUserData(userData)
                    .build();
        }
    }
}

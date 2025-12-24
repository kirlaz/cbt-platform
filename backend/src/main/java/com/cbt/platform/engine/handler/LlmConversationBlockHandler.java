package com.cbt.platform.engine.handler;

import com.cbt.platform.engine.dto.BlockResult;
import com.cbt.platform.engine.dto.BlockType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handler for LLM_CONVERSATION blocks
 * Interactive chat with Claude AI
 * TODO: Integrate with LLM service (claude-3-5-sonnet)
 */
@Component
@Slf4j
public class LlmConversationBlockHandler implements BlockHandler {

    @Override
    public BlockType getBlockType() {
        return BlockType.LLM_CONVERSATION;
    }

    @Override
    public BlockResult handle(JsonNode blockData, JsonNode userData, JsonNode userInput) {
        String blockId = blockData.get("id").asText();
        log.debug("Processing LLM_CONVERSATION block: {}", blockId);

        // TODO: Implement LLM conversation logic
        // - Build system prompt from blockData + userData
        // - Send user message to Claude API
        // - Stream response
        // - Save conversation history to userData

        return BlockResult.builder()
                .blockId(blockId)
                .blockType(BlockType.LLM_CONVERSATION)
                .content(blockData)
                .requiresInput(true)
                .isComplete(false)
                .error("LLM conversation not yet implemented")
                .updatedUserData(userData)
                .build();
    }
}

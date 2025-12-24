package com.cbt.platform.engine.handler;

import com.cbt.platform.engine.dto.BlockResult;
import com.cbt.platform.engine.dto.BlockType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handler for LLM_RESPONSE blocks
 * Generates response based on user data (one-time generation, not chat)
 * TODO: Integrate with LLM service
 */
@Component
@Slf4j
public class LlmResponseBlockHandler implements BlockHandler {

    @Override
    public BlockType getBlockType() {
        return BlockType.LLM_RESPONSE;
    }

    @Override
    public BlockResult handle(JsonNode blockData, JsonNode userData, JsonNode userInput) {
        String blockId = blockData.get("id").asText();
        log.debug("Processing LLM_RESPONSE block: {}", blockId);

        // TODO: Generate response using Claude API with userData context
        return BlockResult.builder()
                .blockId(blockId)
                .blockType(BlockType.LLM_RESPONSE)
                .content(blockData)
                .requiresInput(false)
                .isComplete(true)
                .error("LLM response generation not yet implemented")
                .updatedUserData(userData)
                .build();
    }
}

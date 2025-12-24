package com.cbt.platform.engine.handler;

import com.cbt.platform.engine.dto.BlockResult;
import com.cbt.platform.engine.dto.BlockType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handler for STATIC blocks
 * Displays static content (text, images) without user interaction
 */
@Component
@Slf4j
public class StaticBlockHandler implements BlockHandler {

    @Override
    public BlockType getBlockType() {
        return BlockType.STATIC;
    }

    @Override
    public BlockResult handle(JsonNode blockData, JsonNode userData, JsonNode userInput) {
        log.debug("Processing STATIC block: {}", blockData.get("id"));

        return BlockResult.builder()
                .blockId(blockData.get("id").asText())
                .blockType(BlockType.STATIC)
                .content(blockData.get("messages")) // Display messages from block config
                .requiresInput(false) // Static blocks don't require input
                .isComplete(true) // Always complete (no interaction needed)
                .updatedUserData(userData) // No changes to user data
                .build();
    }
}

package com.cbt.platform.engine.handler;

import com.cbt.platform.engine.dto.BlockResult;
import com.cbt.platform.engine.dto.BlockType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handler for SESSION_COMPLETE blocks
 * Marks session as completed and shows completion message
 */
@Component
@Slf4j
public class SessionCompleteBlockHandler implements BlockHandler {

    @Override
    public BlockType getBlockType() {
        return BlockType.SESSION_COMPLETE;
    }

    @Override
    public BlockResult handle(JsonNode blockData, JsonNode userData, JsonNode userInput) {
        String blockId = blockData.get("id").asText();
        log.debug("Processing SESSION_COMPLETE block: {}", blockId);

        // Session complete blocks are always complete
        return BlockResult.builder()
                .blockId(blockId)
                .blockType(BlockType.SESSION_COMPLETE)
                .content(blockData) // Contains completion message, next session info
                .requiresInput(false)
                .isComplete(true)
                .updatedUserData(userData)
                .build();
    }
}

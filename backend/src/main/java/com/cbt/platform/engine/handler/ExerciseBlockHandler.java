package com.cbt.platform.engine.handler;

import com.cbt.platform.engine.dto.BlockResult;
import com.cbt.platform.engine.dto.BlockType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handler for EXERCISE blocks
 * Interactive CBT exercises (breathing, grounding, thought records, etc.)
 */
@Component
@Slf4j
public class ExerciseBlockHandler implements BlockHandler {

    @Override
    public BlockType getBlockType() {
        return BlockType.EXERCISE;
    }

    @Override
    public BlockResult handle(JsonNode blockData, JsonNode userData, JsonNode userInput) {
        String blockId = blockData.get("id").asText();
        log.debug("Processing EXERCISE block: {}", blockId);

        // For now, just display exercise config
        // TODO: Track exercise completion, save results to userData
        return BlockResult.builder()
                .blockId(blockId)
                .blockType(BlockType.EXERCISE)
                .content(blockData)
                .requiresInput(false)
                .isComplete(true)
                .updatedUserData(userData)
                .build();
    }
}

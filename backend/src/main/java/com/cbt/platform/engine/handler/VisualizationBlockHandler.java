package com.cbt.platform.engine.handler;

import com.cbt.platform.engine.dto.BlockResult;
import com.cbt.platform.engine.dto.BlockType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handler for VISUALIZATION blocks
 * Data visualization (charts, graphs, progress tracking)
 */
@Component
@Slf4j
public class VisualizationBlockHandler implements BlockHandler {

    @Override
    public BlockType getBlockType() {
        return BlockType.VISUALIZATION;
    }

    @Override
    public BlockResult handle(JsonNode blockData, JsonNode userData, JsonNode userInput) {
        String blockId = blockData.get("id").asText();
        log.debug("Processing VISUALIZATION block: {}", blockId);

        // TODO: Process userData and prepare visualization data
        return BlockResult.builder()
                .blockId(blockId)
                .blockType(BlockType.VISUALIZATION)
                .content(blockData)
                .requiresInput(false)
                .isComplete(true)
                .updatedUserData(userData)
                .build();
    }
}

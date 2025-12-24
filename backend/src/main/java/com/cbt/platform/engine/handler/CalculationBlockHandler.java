package com.cbt.platform.engine.handler;

import com.cbt.platform.engine.dto.BlockResult;
import com.cbt.platform.engine.dto.BlockType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handler for CALCULATION blocks
 * Performs calculations on user data (e.g., GAD-7 score, severity levels)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CalculationBlockHandler implements BlockHandler {

    private final ObjectMapper objectMapper;

    @Override
    public BlockType getBlockType() {
        return BlockType.CALCULATION;
    }

    @Override
    public BlockResult handle(JsonNode blockData, JsonNode userData, JsonNode userInput) {
        String blockId = blockData.get("id").asText();
        log.debug("Processing CALCULATION block: {}", blockId);

        // TODO: Implement calculation logic
        // - Extract values from userData based on blockData.input_fields
        // - Apply calculation formula from blockData.formula
        // - Save result to userData at blockData.save_to

        ObjectNode updatedUserData = userData.deepCopy();

        return BlockResult.builder()
                .blockId(blockId)
                .blockType(BlockType.CALCULATION)
                .content(blockData)
                .requiresInput(false)
                .isComplete(true)
                .updatedUserData(updatedUserData)
                .build();
    }
}

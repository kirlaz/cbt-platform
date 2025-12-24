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
 * Handler for SLIDER blocks
 * Collects numeric value via slider and stores in userData
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SliderBlockHandler implements BlockHandler {

    private final ObjectMapper objectMapper;

    @Override
    public BlockType getBlockType() {
        return BlockType.SLIDER;
    }

    @Override
    public BlockResult handle(JsonNode blockData, JsonNode userData, JsonNode userInput) {
        String blockId = blockData.get("id").asText();
        log.debug("Processing SLIDER block: {}", blockId);

        if (userInput == null || userInput.isNull()) {
            return BlockResult.builder()
                    .blockId(blockId)
                    .blockType(BlockType.SLIDER)
                    .content(blockData)
                    .requiresInput(true)
                    .isComplete(false)
                    .updatedUserData(userData)
                    .build();
        }

        String validationError = validateInput(blockData, userInput);
        if (validationError != null) {
            return BlockResult.builder()
                    .blockId(blockId)
                    .blockType(BlockType.SLIDER)
                    .content(blockData)
                    .requiresInput(true)
                    .isComplete(false)
                    .error(validationError)
                    .updatedUserData(userData)
                    .build();
        }

        int value = userInput.get("value").asInt();
        String saveToKey = blockData.get("save_to").asText();

        ObjectNode updatedUserData = userData.deepCopy();
        updatedUserData.put(saveToKey, value);

        log.debug("Saved slider value to userData: {} = {}", saveToKey, value);

        return BlockResult.builder()
                .blockId(blockId)
                .blockType(BlockType.SLIDER)
                .content(blockData)
                .requiresInput(false)
                .isComplete(true)
                .updatedUserData(updatedUserData)
                .build();
    }

    @Override
    public String validateInput(JsonNode blockData, JsonNode userInput) {
        if (!userInput.has("value")) {
            return "Slider value is required";
        }

        int value = userInput.get("value").asInt();
        int min = blockData.get("min").asInt();
        int max = blockData.get("max").asInt();

        if (value < min || value > max) {
            return "Value must be between " + min + " and " + max;
        }

        return null;
    }
}

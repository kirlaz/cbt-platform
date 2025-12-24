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
 * Handler for SINGLE_SELECT blocks
 * User selects one option from multiple choices
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SingleSelectBlockHandler implements BlockHandler {

    private final ObjectMapper objectMapper;

    @Override
    public BlockType getBlockType() {
        return BlockType.SINGLE_SELECT;
    }

    @Override
    public BlockResult handle(JsonNode blockData, JsonNode userData, JsonNode userInput) {
        String blockId = blockData.get("id").asText();
        log.debug("Processing SINGLE_SELECT block: {}", blockId);

        if (userInput == null || userInput.isNull()) {
            return BlockResult.builder()
                    .blockId(blockId)
                    .blockType(BlockType.SINGLE_SELECT)
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
                    .blockType(BlockType.SINGLE_SELECT)
                    .content(blockData)
                    .requiresInput(true)
                    .isComplete(false)
                    .error(validationError)
                    .updatedUserData(userData)
                    .build();
        }

        String selectedOption = userInput.get("selectedOption").asText();
        String saveToKey = blockData.get("save_to").asText();

        ObjectNode updatedUserData = userData.deepCopy();
        updatedUserData.put(saveToKey, selectedOption);

        // Handle conditional next block based on selection
        String nextBlockId = null;
        if (blockData.has("conditional_next")) {
            JsonNode conditionalNext = blockData.get("conditional_next");
            if (conditionalNext.has(selectedOption)) {
                nextBlockId = conditionalNext.get(selectedOption).asText();
            }
        }

        log.debug("Saved selected option to userData: {} = {}", saveToKey, selectedOption);

        return BlockResult.builder()
                .blockId(blockId)
                .blockType(BlockType.SINGLE_SELECT)
                .content(blockData)
                .requiresInput(false)
                .isComplete(true)
                .nextBlockId(nextBlockId)
                .updatedUserData(updatedUserData)
                .build();
    }

    @Override
    public String validateInput(JsonNode blockData, JsonNode userInput) {
        if (!userInput.has("selectedOption")) {
            return "Please select an option";
        }

        String selectedOption = userInput.get("selectedOption").asText();
        JsonNode options = blockData.get("options");

        // Validate that selected option exists
        boolean found = false;
        for (JsonNode option : options) {
            if (option.get("id").asText().equals(selectedOption)) {
                found = true;
                break;
            }
        }

        if (!found) {
            return "Invalid option selected";
        }

        return null;
    }
}

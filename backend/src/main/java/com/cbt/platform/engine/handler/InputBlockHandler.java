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
 * Handler for INPUT blocks
 * Collects text input from user and stores in userData
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class InputBlockHandler implements BlockHandler {

    private final ObjectMapper objectMapper;

    @Override
    public BlockType getBlockType() {
        return BlockType.INPUT;
    }

    @Override
    public BlockResult handle(JsonNode blockData, JsonNode userData, JsonNode userInput) {
        String blockId = blockData.get("id").asText();
        log.debug("Processing INPUT block: {}", blockId);

        // If no user input, return block config for display
        if (userInput == null || userInput.isNull()) {
            return BlockResult.builder()
                    .blockId(blockId)
                    .blockType(BlockType.INPUT)
                    .content(blockData) // Send block config to frontend
                    .requiresInput(true) // Wait for user input
                    .isComplete(false)
                    .updatedUserData(userData)
                    .build();
        }

        // Validate input
        String validationError = validateInput(blockData, userInput);
        if (validationError != null) {
            return BlockResult.builder()
                    .blockId(blockId)
                    .blockType(BlockType.INPUT)
                    .content(blockData)
                    .requiresInput(true)
                    .isComplete(false)
                    .error(validationError)
                    .updatedUserData(userData)
                    .build();
        }

        // Extract input value and save to userData
        String inputValue = userInput.get("value").asText();
        String saveToKey = blockData.get("save_to").asText();

        ObjectNode updatedUserData = userData.deepCopy();
        updatedUserData.put(saveToKey, inputValue);

        log.debug("Saved input to userData: {} = {}", saveToKey, inputValue);

        return BlockResult.builder()
                .blockId(blockId)
                .blockType(BlockType.INPUT)
                .content(blockData)
                .requiresInput(false)
                .isComplete(true)
                .updatedUserData(updatedUserData)
                .build();
    }

    @Override
    public String validateInput(JsonNode blockData, JsonNode userInput) {
        if (!userInput.has("value")) {
            return "Input value is required";
        }

        String value = userInput.get("value").asText();

        // Check if required
        boolean required = blockData.has("required") && blockData.get("required").asBoolean();
        if (required && (value == null || value.trim().isEmpty())) {
            return "This field is required";
        }

        // Check min length
        if (blockData.has("min_length")) {
            int minLength = blockData.get("min_length").asInt();
            if (value.length() < minLength) {
                return "Minimum length is " + minLength + " characters";
            }
        }

        // Check max length
        if (blockData.has("max_length")) {
            int maxLength = blockData.get("max_length").asInt();
            if (value.length() > maxLength) {
                return "Maximum length is " + maxLength + " characters";
            }
        }

        return null; // Valid
    }
}

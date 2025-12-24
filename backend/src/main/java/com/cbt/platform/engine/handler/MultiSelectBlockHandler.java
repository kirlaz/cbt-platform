package com.cbt.platform.engine.handler;

import com.cbt.platform.engine.dto.BlockResult;
import com.cbt.platform.engine.dto.BlockType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Handler for MULTI_SELECT blocks
 * User selects multiple options from choices
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MultiSelectBlockHandler implements BlockHandler {

    private final ObjectMapper objectMapper;

    @Override
    public BlockType getBlockType() {
        return BlockType.MULTI_SELECT;
    }

    @Override
    public BlockResult handle(JsonNode blockData, JsonNode userData, JsonNode userInput) {
        String blockId = blockData.get("id").asText();
        log.debug("Processing MULTI_SELECT block: {}", blockId);

        if (userInput == null || userInput.isNull()) {
            return BlockResult.builder()
                    .blockId(blockId)
                    .blockType(BlockType.MULTI_SELECT)
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
                    .blockType(BlockType.MULTI_SELECT)
                    .content(blockData)
                    .requiresInput(true)
                    .isComplete(false)
                    .error(validationError)
                    .updatedUserData(userData)
                    .build();
        }

        ArrayNode selectedOptions = (ArrayNode) userInput.get("selectedOptions");
        String saveToKey = blockData.get("save_to").asText();

        ObjectNode updatedUserData = userData.deepCopy();
        updatedUserData.set(saveToKey, selectedOptions);

        log.debug("Saved selected options to userData: {} = {}", saveToKey, selectedOptions);

        return BlockResult.builder()
                .blockId(blockId)
                .blockType(BlockType.MULTI_SELECT)
                .content(blockData)
                .requiresInput(false)
                .isComplete(true)
                .updatedUserData(updatedUserData)
                .build();
    }

    @Override
    public String validateInput(JsonNode blockData, JsonNode userInput) {
        if (!userInput.has("selectedOptions")) {
            return "Please select at least one option";
        }

        JsonNode selectedOptions = userInput.get("selectedOptions");
        if (!selectedOptions.isArray() || selectedOptions.size() == 0) {
            return "Please select at least one option";
        }

        // Validate that all selected options exist
        JsonNode options = blockData.get("options");
        Set<String> validOptionIds = new HashSet<>();
        for (JsonNode option : options) {
            validOptionIds.add(option.get("id").asText());
        }

        for (JsonNode selected : selectedOptions) {
            String selectedId = selected.asText();
            if (!validOptionIds.contains(selectedId)) {
                return "Invalid option selected: " + selectedId;
            }
        }

        // Check min/max selections if specified
        if (blockData.has("min_selections")) {
            int minSelections = blockData.get("min_selections").asInt();
            if (selectedOptions.size() < minSelections) {
                return "Please select at least " + minSelections + " options";
            }
        }

        if (blockData.has("max_selections")) {
            int maxSelections = blockData.get("max_selections").asInt();
            if (selectedOptions.size() > maxSelections) {
                return "Please select at most " + maxSelections + " options";
            }
        }

        return null;
    }
}

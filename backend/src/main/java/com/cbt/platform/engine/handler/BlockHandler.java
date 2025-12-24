package com.cbt.platform.engine.handler;

import com.cbt.platform.engine.dto.BlockResult;
import com.cbt.platform.engine.dto.BlockType;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface for block handlers
 * Each block type has its own handler implementation
 * Strategy pattern: BlockHandlerRegistry maps BlockType to Handler
 */
public interface BlockHandler {

    /**
     * Get the block type this handler supports
     */
    BlockType getBlockType();

    /**
     * Process block and return result
     *
     * @param blockData   Block configuration from scenario JSON
     * @param userData    Current user data (JSONB from UserProgress)
     * @param userInput   User input (null if first rendering, non-null if user submitted data)
     * @return BlockResult with content to display and updated user data
     */
    BlockResult handle(JsonNode blockData, JsonNode userData, JsonNode userInput);

    /**
     * Validate user input for this block type
     *
     * @param blockData Block configuration
     * @param userInput User input
     * @return Validation error message, or null if valid
     */
    default String validateInput(JsonNode blockData, JsonNode userInput) {
        return null; // Override in handlers that need validation
    }
}

package com.cbt.platform.engine.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of block processing by a handler
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockResult {

    /**
     * Block ID that was processed
     */
    private String blockId;

    /**
     * Block type
     */
    private BlockType blockType;

    /**
     * Content to display to user (messages, UI components, etc.)
     */
    private JsonNode content;

    /**
     * Whether user interaction is required
     */
    @Builder.Default
    private boolean requiresInput = false;

    /**
     * Whether block processing is complete
     */
    @Builder.Default
    private boolean isComplete = false;

    /**
     * Next block ID to navigate to (if specified by block logic)
     */
    private String nextBlockId;

    /**
     * Updated user data after block processing
     */
    private JsonNode updatedUserData;

    /**
     * Error message (if block processing failed)
     */
    private String error;

    /**
     * Additional metadata (validation errors, completion status, etc.)
     */
    private JsonNode metadata;
}

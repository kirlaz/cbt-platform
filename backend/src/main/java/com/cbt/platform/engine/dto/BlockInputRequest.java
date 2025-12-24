package com.cbt.platform.engine.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for submitting user input to a block
 */
public record BlockInputRequest(
        @NotBlank(message = "Block ID is required")
        String blockId,

        /**
         * User input data (format depends on block type)
         * - INPUT: {"value": "text"}
         * - SLIDER: {"value": 7}
         * - SINGLE_SELECT: {"selectedOption": "option_id"}
         * - MULTI_SELECT: {"selectedOptions": ["option1", "option2"]}
         * - LLM_CONVERSATION: {"message": "user message"}
         */
        JsonNode input
) {
}

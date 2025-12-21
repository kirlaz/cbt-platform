package com.cbt.platform.editor.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new block template
 */
public record CreateBlockTemplateRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 200, message = "Name must not exceed 200 characters")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotBlank(message = "Block type is required")
        @Size(max = 50, message = "Block type must not exceed 50 characters")
        String blockType,

        @Size(max = 50, message = "Category must not exceed 50 characters")
        String category,

        @NotNull(message = "Block JSON is required")
        JsonNode blockJson
) {
}

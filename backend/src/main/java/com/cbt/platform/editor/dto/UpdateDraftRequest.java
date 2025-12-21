package com.cbt.platform.editor.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing scenario draft
 */
public record UpdateDraftRequest(
        @Size(max = 200, message = "Name must not exceed 200 characters")
        String name,

        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
        @Size(max = 100, message = "Slug must not exceed 100 characters")
        String slug,

        @Size(max = 50, message = "Category must not exceed 50 characters")
        String category,

        @Size(max = 20, message = "Version must not exceed 20 characters")
        String version,

        JsonNode scenarioJson,

        @Size(max = 500, message = "Change description must not exceed 500 characters")
        String changeDescription
) {
}

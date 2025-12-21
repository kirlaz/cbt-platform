package com.cbt.platform.editor.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for block template
 */
public record BlockTemplateResponse(
        UUID id,
        String name,
        String description,
        String blockType,
        String category,
        JsonNode blockJson,
        boolean isSystem,
        boolean isActive,
        Integer usageCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

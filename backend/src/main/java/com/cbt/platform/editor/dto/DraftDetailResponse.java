package com.cbt.platform.editor.dto;

import com.cbt.platform.editor.entity.DraftStatus;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for scenario draft detail view (full data)
 */
public record DraftDetailResponse(
        UUID id,
        String name,
        String slug,
        String category,
        String version,
        JsonNode scenarioJson,
        DraftStatus status,
        boolean isValid,
        JsonNode validationErrors,
        UUID createdByUserId,
        String createdByName,
        UUID lastModifiedByUserId,
        String lastModifiedByName,
        UUID publishedCourseId,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

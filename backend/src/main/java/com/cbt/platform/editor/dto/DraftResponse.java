package com.cbt.platform.editor.dto;

import com.cbt.platform.editor.entity.DraftStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for scenario draft list view (lightweight)
 */
public record DraftResponse(
        UUID id,
        String name,
        String slug,
        String category,
        String version,
        DraftStatus status,
        boolean isValid,
        String createdByName,
        String lastModifiedByName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime publishedAt
) {
}

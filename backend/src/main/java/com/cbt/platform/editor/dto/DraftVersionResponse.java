package com.cbt.platform.editor.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for scenario draft version
 */
public record DraftVersionResponse(
        UUID id,
        UUID draftId,
        Integer versionNumber,
        JsonNode scenarioJson,
        String changeDescription,
        UUID createdByUserId,
        String createdByName,
        LocalDateTime createdAt
) {
}

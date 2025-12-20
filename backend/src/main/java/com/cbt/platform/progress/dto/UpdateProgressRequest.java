package com.cbt.platform.progress.dto;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Request DTO for updating user progress
 */
public record UpdateProgressRequest(
        String currentSessionId,
        Integer currentBlockIndex,
        JsonNode userData,
        Integer completionPercentage
) {
}

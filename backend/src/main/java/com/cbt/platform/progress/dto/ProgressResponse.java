package com.cbt.platform.progress.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for user progress data
 */
public record ProgressResponse(
        UUID id,
        UUID userId,
        UUID courseId,
        String currentSessionId,
        Integer currentBlockIndex,
        JsonNode userData,
        List<String> completedSessions,
        List<String> completedBlocks,
        Integer completionPercentage,
        boolean isCompleted,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        LocalDateTime lastActivityAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

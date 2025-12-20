package com.cbt.platform.course.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for course detail view (includes full scenario JSON)
 */
public record CourseDetailResponse(
        UUID id,
        String slug,
        String name,
        String description,
        JsonNode scenarioJson,
        String version,
        Integer freeSessions,
        BigDecimal price,
        String imageUrl,
        Integer estimatedDurationMinutes,
        String category,
        boolean isActive,
        boolean isPublished,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

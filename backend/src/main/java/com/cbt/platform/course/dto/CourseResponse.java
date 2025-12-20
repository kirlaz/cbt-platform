package com.cbt.platform.course.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for course list view (lightweight)
 */
public record CourseResponse(
        UUID id,
        String slug,
        String name,
        String description,
        String version,
        Integer freeSessions,
        BigDecimal price,
        String imageUrl,
        Integer estimatedDurationMinutes,
        String category,
        boolean isActive,
        boolean isPublished,
        LocalDateTime createdAt
) {
}

package com.cbt.platform.course.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Request DTO for updating an existing course
 */
public record UpdateCourseRequest(
        @Size(max = 200, message = "Name must not exceed 200 characters")
        String name,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        JsonNode scenarioJson,

        @Size(max = 20, message = "Version must not exceed 20 characters")
        String version,

        @Min(value = 0, message = "Free sessions must be non-negative")
        Integer freeSessions,

        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
        BigDecimal price,

        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        String imageUrl,

        @Min(value = 1, message = "Estimated duration must be at least 1 minute")
        Integer estimatedDurationMinutes,

        @Size(max = 50, message = "Category must not exceed 50 characters")
        String category,

        Boolean isActive,

        Boolean isPublished
) {
}

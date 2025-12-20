package com.cbt.platform.course.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new course
 */
public record CreateCourseRequest(
        @NotBlank(message = "Slug is required")
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
        @Size(max = 100, message = "Slug must not exceed 100 characters")
        String slug,

        @NotBlank(message = "Name is required")
        @Size(max = 200, message = "Name must not exceed 200 characters")
        String name,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @NotNull(message = "Scenario JSON is required")
        JsonNode scenarioJson,

        @NotBlank(message = "Version is required")
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

        Boolean isPublished
) {
}

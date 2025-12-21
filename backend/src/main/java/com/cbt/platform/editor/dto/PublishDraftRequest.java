package com.cbt.platform.editor.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Request DTO for publishing a draft to a course
 */
public record PublishDraftRequest(
        @Min(value = 0, message = "Free sessions must be non-negative")
        Integer freeSessions,

        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
        BigDecimal price,

        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        String imageUrl,

        @Min(value = 1, message = "Estimated duration must be at least 1 minute")
        Integer estimatedDurationMinutes,

        Boolean isPublished
) {
}

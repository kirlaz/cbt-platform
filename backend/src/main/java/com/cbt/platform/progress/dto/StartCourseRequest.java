package com.cbt.platform.progress.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request DTO for starting a course
 */
public record StartCourseRequest(
        @NotNull(message = "Course ID is required")
        UUID courseId
) {
}

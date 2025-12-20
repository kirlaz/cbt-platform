package com.cbt.platform.progress.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for marking a session as completed
 */
public record CompleteSessionRequest(
        @NotBlank(message = "Session ID is required")
        String sessionId
) {
}

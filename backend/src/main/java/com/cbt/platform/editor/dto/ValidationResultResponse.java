package com.cbt.platform.editor.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Response DTO for scenario validation result
 */
public record ValidationResultResponse(
        boolean isValid,
        List<ValidationError> errors,
        JsonNode details
) {
    public record ValidationError(
            String code,
            String message,
            String field,
            String severity
    ) {
    }
}

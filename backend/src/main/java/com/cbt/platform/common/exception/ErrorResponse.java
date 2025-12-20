package com.cbt.platform.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Standard error response structure for API errors
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp,
        String path
) {
    /**
     * Simple constructor with code and message
     */
    public ErrorResponse(String code, String message) {
        this(code, message, LocalDateTime.now(), null);
    }

    /**
     * Constructor with code, message, and path
     */
    public ErrorResponse(String code, String message, String path) {
        this(code, message, LocalDateTime.now(), path);
    }
}

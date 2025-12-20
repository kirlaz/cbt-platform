package com.cbt.platform.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for all business exceptions in the platform
 * All module-specific exceptions should extend this class
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    /**
     * Constructor for BaseException
     *
     * @param message Error message
     * @param code    Error code (e.g., "USER_NOT_FOUND")
     * @param status  HTTP status code
     */
    protected BaseException(String message, String code, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    /**
     * Constructor with cause
     *
     * @param message Error message
     * @param code    Error code
     * @param status  HTTP status code
     * @param cause   The cause of the exception
     */
    protected BaseException(String message, String code, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = status;
    }
}

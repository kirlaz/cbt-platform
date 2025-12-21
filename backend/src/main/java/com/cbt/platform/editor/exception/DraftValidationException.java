package com.cbt.platform.editor.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when draft validation fails
 */
public class DraftValidationException extends BaseException {

    public DraftValidationException(String message) {
        super("Draft validation failed: " + message, "DRAFT_VALIDATION_FAILED", HttpStatus.BAD_REQUEST);
    }
}

package com.cbt.platform.editor.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when draft publishing fails
 */
public class DraftPublishException extends BaseException {

    public DraftPublishException(String message) {
        super("Draft publishing failed: " + message, "DRAFT_PUBLISH_FAILED", HttpStatus.BAD_REQUEST);
    }
}

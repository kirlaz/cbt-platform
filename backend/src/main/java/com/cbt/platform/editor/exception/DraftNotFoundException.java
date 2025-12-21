package com.cbt.platform.editor.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Exception thrown when a scenario draft is not found
 */
public class DraftNotFoundException extends BaseException {

    public DraftNotFoundException(UUID id) {
        super("Draft not found: " + id, "DRAFT_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public DraftNotFoundException(String slug) {
        super("Draft not found: " + slug, "DRAFT_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}

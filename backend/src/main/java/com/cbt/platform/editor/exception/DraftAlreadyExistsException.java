package com.cbt.platform.editor.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to create a draft with a slug that already exists
 */
public class DraftAlreadyExistsException extends BaseException {

    public DraftAlreadyExistsException(String slug) {
        super("Draft with slug already exists: " + slug, "DRAFT_ALREADY_EXISTS", HttpStatus.CONFLICT);
    }
}

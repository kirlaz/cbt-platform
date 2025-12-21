package com.cbt.platform.editor.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Exception thrown when a draft version is not found
 */
public class VersionNotFoundException extends BaseException {

    public VersionNotFoundException(UUID draftId, Integer versionNumber) {
        super("Version not found: " + versionNumber + " for draft: " + draftId,
              "VERSION_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}

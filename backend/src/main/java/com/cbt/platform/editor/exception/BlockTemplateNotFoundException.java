package com.cbt.platform.editor.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Exception thrown when a block template is not found
 */
public class BlockTemplateNotFoundException extends BaseException {

    public BlockTemplateNotFoundException(UUID id) {
        super("Block template not found: " + id, "BLOCK_TEMPLATE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}

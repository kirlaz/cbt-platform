package com.cbt.platform.engine.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a block is not found in scenario
 */
public class BlockNotFoundException extends BaseException {

    public BlockNotFoundException(String blockId) {
        super("Block not found: " + blockId, "BLOCK_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public BlockNotFoundException(String sessionId, String blockId) {
        super("Block not found: " + blockId + " in session: " + sessionId,
                "BLOCK_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}

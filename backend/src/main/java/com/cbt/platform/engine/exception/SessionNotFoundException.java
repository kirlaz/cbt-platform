package com.cbt.platform.engine.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a session is not found in course scenario
 */
public class SessionNotFoundException extends BaseException {

    public SessionNotFoundException(String sessionId) {
        super("Session not found: " + sessionId, "SESSION_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}

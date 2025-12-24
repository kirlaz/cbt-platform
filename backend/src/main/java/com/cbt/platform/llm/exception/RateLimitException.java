package com.cbt.platform.llm.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when LLM provider rate limit is exceeded
 */
public class RateLimitException extends BaseException {

    public RateLimitException() {
        super("Rate limit exceeded. Please try again later.",
                "RATE_LIMIT_EXCEEDED",
                HttpStatus.TOO_MANY_REQUESTS);
    }

    public RateLimitException(String message) {
        super(message, "RATE_LIMIT_EXCEEDED", HttpStatus.TOO_MANY_REQUESTS);
    }
}

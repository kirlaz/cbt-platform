package com.cbt.platform.llm.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when LLM provider encounters an error
 */
public class LlmProviderException extends BaseException {

    public LlmProviderException(String message) {
        super(message, "LLM_PROVIDER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public LlmProviderException(String message, Throwable cause) {
        super(message, "LLM_PROVIDER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }

    public LlmProviderException(String message, HttpStatus status) {
        super(message, "LLM_PROVIDER_ERROR", status);
    }
}

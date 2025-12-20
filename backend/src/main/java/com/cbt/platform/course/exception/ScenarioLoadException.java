package com.cbt.platform.course.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when scenario JSON file cannot be loaded or parsed
 */
public class ScenarioLoadException extends BaseException {

    public ScenarioLoadException(String path, Throwable cause) {
        super("Failed to load scenario from: " + path, "SCENARIO_LOAD_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }

    public ScenarioLoadException(String message) {
        super(message, "SCENARIO_LOAD_ERROR", HttpStatus.BAD_REQUEST);
    }
}

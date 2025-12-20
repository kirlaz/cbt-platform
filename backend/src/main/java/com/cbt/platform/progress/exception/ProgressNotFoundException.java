package com.cbt.platform.progress.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Exception thrown when user progress is not found
 */
public class ProgressNotFoundException extends BaseException {

    public ProgressNotFoundException(UUID userId, UUID courseId) {
        super(
                String.format("Progress not found for user %s in course %s", userId, courseId),
                "PROGRESS_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }

    public ProgressNotFoundException(UUID progressId) {
        super("Progress not found: " + progressId, "PROGRESS_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}

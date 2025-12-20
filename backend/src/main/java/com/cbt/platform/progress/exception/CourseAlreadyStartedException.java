package com.cbt.platform.progress.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Exception thrown when user tries to start a course they've already started
 */
public class CourseAlreadyStartedException extends BaseException {

    public CourseAlreadyStartedException(UUID userId, UUID courseId) {
        super(
                String.format("User %s has already started course %s", userId, courseId),
                "COURSE_ALREADY_STARTED",
                HttpStatus.CONFLICT
        );
    }
}

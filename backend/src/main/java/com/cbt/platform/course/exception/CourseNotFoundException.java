package com.cbt.platform.course.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Exception thrown when a course is not found
 */
public class CourseNotFoundException extends BaseException {

    public CourseNotFoundException(UUID id) {
        super("Course not found: " + id, "COURSE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public CourseNotFoundException(String slug) {
        super("Course not found: " + slug, "COURSE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}

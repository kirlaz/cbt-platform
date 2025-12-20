package com.cbt.platform.course.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to create a course with a slug that already exists
 */
public class CourseAlreadyExistsException extends BaseException {

    public CourseAlreadyExistsException(String slug) {
        super("Course already exists with slug: " + slug, "COURSE_ALREADY_EXISTS", HttpStatus.CONFLICT);
    }
}

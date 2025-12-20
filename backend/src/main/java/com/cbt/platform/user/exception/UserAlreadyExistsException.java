package com.cbt.platform.user.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to register with an email that already exists
 */
public class UserAlreadyExistsException extends BaseException {

    public UserAlreadyExistsException(String email) {
        super("User already exists with email: " + email, "USER_ALREADY_EXISTS", HttpStatus.CONFLICT);
    }
}

package com.cbt.platform.user.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Exception thrown when a user is not found
 */
public class UserNotFoundException extends BaseException {

    public UserNotFoundException(UUID id) {
        super("User not found: " + id, "USER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public UserNotFoundException(String email) {
        super("User not found: " + email, "USER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}

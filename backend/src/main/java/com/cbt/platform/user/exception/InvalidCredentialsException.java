package com.cbt.platform.user.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when login credentials are invalid
 */
public class InvalidCredentialsException extends BaseException {

    public InvalidCredentialsException() {
        super("Invalid email or password", "INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED);
    }
}

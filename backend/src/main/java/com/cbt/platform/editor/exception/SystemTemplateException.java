package com.cbt.platform.editor.exception;

import com.cbt.platform.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to modify or delete a system block template
 */
public class SystemTemplateException extends BaseException {

    public SystemTemplateException() {
        super("Cannot modify or delete system templates", "SYSTEM_TEMPLATE_PROTECTED", HttpStatus.FORBIDDEN);
    }
}

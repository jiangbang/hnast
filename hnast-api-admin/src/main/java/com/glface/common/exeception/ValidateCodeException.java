package com.glface.common.exeception;

import org.springframework.security.core.AuthenticationException;

public class ValidateCodeException extends AuthenticationException {
    public ValidateCodeException(String message) {
        super(message);
    }
}

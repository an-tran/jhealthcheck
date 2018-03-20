package com.antt.security;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by antt on 3/20/2018.
 */

public class UserDisabledException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public UserDisabledException(String message) {
        super(message);
    }

    public UserDisabledException(String message, Throwable t) {
        super(message, t);
    }
}

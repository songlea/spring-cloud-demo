package com.songlea.demo.cloud.security.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * InvalidJwtTokenException
 */
public class InvalidJwtTokenException extends AuthenticationException {

    private static final long serialVersionUID = -294671188037098603L;

    public InvalidJwtTokenException(String msg) {
        super(msg);
    }
}

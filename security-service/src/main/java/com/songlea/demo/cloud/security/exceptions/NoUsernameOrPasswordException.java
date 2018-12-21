package com.songlea.demo.cloud.security.exceptions;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * NoUsernameOrPasswordException
 */
public class NoUsernameOrPasswordException extends AuthenticationServiceException {

    private static final long serialVersionUID = -802591077020459405L;

    public NoUsernameOrPasswordException(String msg) {
        super(msg);
    }
}

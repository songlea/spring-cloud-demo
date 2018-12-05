package com.songlea.demo.cloud.security.exceptions;

import com.songlea.demo.cloud.security.model.token.JwtToken;
import org.springframework.security.core.AuthenticationException;

/**
 * JwtTokenExpiredException
 */
public class JwtTokenExpiredException extends AuthenticationException {

    private static final long serialVersionUID = -5959543783324224864L;

    private JwtToken token;

    public JwtTokenExpiredException(String msg) {
        super(msg);
    }

    public JwtTokenExpiredException(JwtToken token, String msg, Throwable t) {
        super(msg, t);
        this.token = token;
    }

    public String token() {
        return this.token.getToken();
    }

}

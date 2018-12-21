package com.songlea.demo.cloud.security.auth.jwt.verifier;

/**
 * TokenVerifier
 */
public interface TokenVerifier {

    boolean verify(String jti);
}

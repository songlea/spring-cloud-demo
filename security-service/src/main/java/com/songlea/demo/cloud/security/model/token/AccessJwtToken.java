package com.songlea.demo.cloud.security.model.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jsonwebtoken.Claims;

/**
 * AccessJwtToken
 */
public final class AccessJwtToken implements JwtToken {

    private final String rawToken;

    @JsonIgnore
    private Claims claims;

    public AccessJwtToken(final String token, Claims claims) {
        this.rawToken = token;
        this.claims = claims;
    }

    public String getToken() {
        return this.rawToken;
    }

    public Claims getClaims() {
        return claims;
    }

}

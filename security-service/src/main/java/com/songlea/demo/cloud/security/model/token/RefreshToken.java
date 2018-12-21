package com.songlea.demo.cloud.security.model.token;

import com.songlea.demo.cloud.security.model.Scopes;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.List;
import java.util.Optional;

/**
 * RefreshToken
 */
public class RefreshToken implements JwtToken {

    /*
    Claims
        iss:"iss"(issuer) claim 定义了发布这个 JWT 的当事人,这个claim是OPTIONAL
        sub:"sub"(subject) claim 定义了这个 JWT 的 subject,值是大小写敏感,这个claim是OPTIONAL
        aud:"aud"(audience) claim 在 JWT 中用来标识收件人,这个claim是OPTIONAL
        exp:"exp"(expiration time) claim 标识了失效时间,这个claim是OPTIONAL
        nbf:"nbf"(not before) claim 标识了时间点,当早于这个时间点,JWT 必须不被接受和处理,这个claim是OPTIONAL
        iat:"iat"(issued at) claim 标识了 JWT 所颁发的时间,这个claim是OPTIONAL
        jti:"jti"(JWT ID) claim 作为 JWT 的唯一标识符,值是大小写敏感,这个claim是OPTIONAL
     */
    private Jws<Claims> claims;

    private RefreshToken(Jws<Claims> claims) {
        this.claims = claims;
    }

    @SuppressWarnings("unchecked")
    public static Optional<RefreshToken> create(RawAccessJwtToken token, String signingKey) {
        Jws<Claims> claims = token.parseClaims(signingKey);

        List<String> scopes = claims.getBody().get("scopes", List.class);
        if (scopes == null || scopes.isEmpty()
                || scopes.stream().noneMatch(scope -> Scopes.REFRESH_TOKEN.authority().equals(scope))) {
            return Optional.empty();
        }

        return Optional.of(new RefreshToken(claims));
    }

    @Override
    public String getToken() {
        return  null;
    }

    public Jws<Claims> getClaims() {
        return claims;
    }

    public String getJti() {
        return claims.getBody().getId();
    }

    public String getSubject() {
        return claims.getBody().getSubject();
    }

}

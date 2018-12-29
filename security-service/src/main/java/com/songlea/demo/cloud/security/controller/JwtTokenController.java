package com.songlea.demo.cloud.security.controller;

import com.songlea.demo.cloud.security.auth.jwt.JwtAuthenticationToken;
import com.songlea.demo.cloud.security.auth.jwt.extractor.TokenExtractor;
import com.songlea.demo.cloud.security.auth.jwt.factory.JwtTokenFactoryImpl;
import com.songlea.demo.cloud.security.auth.jwt.verifier.TokenVerifier;
import com.songlea.demo.cloud.security.auth.userdetails.CustomUserDetails;
import com.songlea.demo.cloud.security.auth.userdetails.ExtendUserDetailsService;
import com.songlea.demo.cloud.security.auth.CustomHandlerURL;
import com.songlea.demo.cloud.security.config.JwtSettings;
import com.songlea.demo.cloud.security.exceptions.InvalidJwtTokenException;
import com.songlea.demo.cloud.security.model.token.JwtToken;
import com.songlea.demo.cloud.security.model.token.RawAccessJwtToken;
import com.songlea.demo.cloud.security.model.token.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * RefreshTokenEndpoint
 */
@RestController
public class JwtTokenController {

    @Autowired
    private JwtTokenFactoryImpl tokenFactory;

    @Autowired
    private JwtSettings jwtSettings;

    @Autowired
    private ExtendUserDetailsService extendUserDetailsService;

    @Autowired
    private TokenVerifier tokenVerifier;

    @Autowired
    private TokenExtractor tokenExtractor;

    @RequestMapping(value = CustomHandlerURL.REFRESH_TOKEN_URL, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public JwtToken refreshToken(HttpServletRequest request) {
        String tokenPayload = tokenExtractor.extract(request.getHeader(CustomHandlerURL.AUTHENTICATION_HEADER_NAME));

        RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);
        RefreshToken refreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey())
                .orElseThrow(() -> new InvalidJwtTokenException("Invalid jwt token"));
        // 验证token的jti(唯一标识符)
        String jti = refreshToken.getJti();
        if (!tokenVerifier.verify(jti)) {
            throw new InvalidJwtTokenException("Invalid jwt token");
        }
        String subject = refreshToken.getSubject();
        UserDetails userDetails = extendUserDetailsService.loadUserByUsername(subject);
        // 默认实现的CustomUserDetailsService里返回的是CustomUserDetails
        CustomUserDetails.UserContext userContext = ((CustomUserDetails) userDetails).builderUserContext();
        return tokenFactory.createAccessJwtToken(userContext);

    }

    @RequestMapping(value = "/api/me", method = RequestMethod.GET)
    public CustomUserDetails.UserContext get(JwtAuthenticationToken token) {
        return (CustomUserDetails.UserContext) token.getPrincipal();
    }
}

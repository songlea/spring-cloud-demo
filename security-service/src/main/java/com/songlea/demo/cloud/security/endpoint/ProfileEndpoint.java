package com.songlea.demo.cloud.security.endpoint;

import com.songlea.demo.cloud.security.auth.jwt.JwtAuthenticationToken;
import com.songlea.demo.cloud.security.model.UserContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProfileEndpoint
 */
@RestController
public class ProfileEndpoint {

    @RequestMapping(value = "/api/me", method = RequestMethod.GET)
    public UserContext get(JwtAuthenticationToken token) {
        return (UserContext) token.getPrincipal();
    }

}

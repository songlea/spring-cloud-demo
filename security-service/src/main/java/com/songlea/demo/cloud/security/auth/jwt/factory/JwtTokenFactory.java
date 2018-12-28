package com.songlea.demo.cloud.security.auth.jwt.factory;

import com.songlea.demo.cloud.security.auth.userdetails.CustomUserDetails;
import com.songlea.demo.cloud.security.model.token.AccessJwtToken;
import com.songlea.demo.cloud.security.model.token.JwtToken;

/**
 * jwt Token相关接口
 */
public interface JwtTokenFactory {

    AccessJwtToken createAccessJwtToken(CustomUserDetails.UserContext userContext);

    JwtToken createRefreshToken(CustomUserDetails.UserContext userContext);

}

package com.songlea.demo.cloud.security.auth.jwt.extractor;

import com.songlea.demo.cloud.security.exceptions.InvalidJwtTokenException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * JwtHeaderTokenExtractor通常用来扩展来处理身份检验的处理,
 * 扩展TokenExtractor 接口 和 提供你常用的一些实现
 */
@Component
public class JwtHeaderTokenExtractor implements TokenExtractor {

    @Override
    public String extract(String header) {
        if (StringUtils.isBlank(header)) {
            throw new InvalidJwtTokenException("Authorization header cannot be blank!");
        }

        if (header.length() < JWT_HEADER_PREFIX.length()) {
            throw new InvalidJwtTokenException("Invalid authorization header size.");
        }
        return header.substring(JWT_HEADER_PREFIX.length());
    }

}

package com.songlea.demo.cloud.security.auth.jwt.extractor;

/**
 * 用于token解析
 */
public interface TokenExtractor {

    String JWT_HEADER_PREFIX = "Bearer ";

    String extract(String payload);

}

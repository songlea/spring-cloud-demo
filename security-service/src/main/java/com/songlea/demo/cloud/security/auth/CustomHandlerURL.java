package com.songlea.demo.cloud.security.auth;

/**
 * URL常量
 *
 * @author Song Lea
 */
public class CustomHandlerURL {

    public static final String AUTHENTICATION_HEADER_NAME = "Authorization";

    // 不需要登录验证的
    public static final String SYS_USER_INSERT_URL = "/sys-user/insert";
    public static final String AUTHENTICATION_URL = "/api/auth/login";
    public static final String REFRESH_TOKEN_URL = "/api/auth/token/refresh";
    public static final String ERROR_URL = "/error";

    // 需要鉴权的
    // public static final String API_ROOT_URL = "/api/**";
    public static final String API_ROOT_URL = "/**";

}

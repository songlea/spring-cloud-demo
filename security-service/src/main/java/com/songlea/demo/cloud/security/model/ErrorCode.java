package com.songlea.demo.cloud.security.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ErrorCode:验证错误时本地的错误编码
 */
public enum ErrorCode {

    GLOBAL(1),
    AUTHENTICATION_FAILED(10),
    INVALID_USERNAME_OR_PASSWORD(11),
    AUTHENTICATION_METHOD_NOT_SUPPORTED(12),
    USER_HAS_NO_ROLES_ASSIGNED(13),
    USER_ACCOUNT_IS_LOCKED_OR_DISABLE(14),
    USER_ACCOUNT_OR_PASSWORD_HAS_EXPIRED(15),
    TOKEN_HAS_EXPIRED_OR_INVALID(16),
    FORBIDDEN(20);

    private int errorCode;

    ErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    // @JsonValue 可以用在get方法或者属性字段上,一个类只能用一个,当加上@JsonValue注解时序列化是只返回这一个字段的值
    @JsonValue
    public int getErrorCode() {
        return errorCode;
    }
}

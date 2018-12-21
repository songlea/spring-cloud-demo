package com.songlea.demo.cloud.security.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ErrorCode
 */
public enum ErrorCode {

    GLOBAL(2), AUTHENTICATION(10), JWT_TOKEN_EXPIRED_OR_INVALID(11);

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

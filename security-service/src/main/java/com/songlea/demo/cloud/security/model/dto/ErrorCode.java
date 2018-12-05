package com.songlea.demo.cloud.security.model.dto;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ErrorCode
 */
public enum ErrorCode {

    GLOBAL(2), AUTHENTICATION(10), JWT_TOKEN_EXPIRED(11);

    private int errorCode;

    ErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @JsonValue
    public int getErrorCode() {
        return errorCode;
    }

}

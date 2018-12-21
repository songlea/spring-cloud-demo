package com.songlea.demo.cloud.security.model;

import org.springframework.http.HttpStatus;

import java.util.Date;

/**
 * Error model for interacting with client.
 */
public class ErrorResponse {

    public static final String INVALID_USERNAME_OR_PASSWORD = "invalid_username_or_password";
    public static final String AUTHENTICATION_METHOD_NOT_SUPPORTED = "authentication_method_not_supported";
    public static final String USER_HAS_NO_ROLES_ASSIGNED = "user_has_no_roles_assigned";
    public static final String TOKEN_HAS_EXPIRED_OR_INVALID = "token_has_expired_or_invalid";
    public static final String AUTHENTICATION_FAILED = "authentication_failed";
    public static final String USER_ACCOUNT_IS_LOCKED = "user_account_is_locked";
    public static final String USER_ACCOUNT_OR_PASSWORD_HAS_EXPIRED = "user_account_or_password_has_expired";

    // HTTP Response Status Code
    private final HttpStatus status;

    // General Error message
    private final String message;

    // Error code
    private final ErrorCode errorCode;

    private final Date timestamp;

    protected ErrorResponse(final String message, final ErrorCode errorCode, HttpStatus status) {
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
        this.timestamp = new Date();
    }

    public static ErrorResponse of(final String message, final ErrorCode errorCode, HttpStatus status) {
        return new ErrorResponse(message, errorCode, status);
    }

    public Integer getStatus() {
        return status.value();
    }

    public String getMessage() {
        return message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}

package com.loveyue.auth.exception;

/**
 * 令牌无效异常
 * 
 * @author loveyue
 * @since 2025-07-13
 */
public class InvalidTokenException extends AuthException {

    public InvalidTokenException(String message) {
        super("INVALID_TOKEN", message);
    }

    public InvalidTokenException(String message, Object... args) {
        super("INVALID_TOKEN", message, args);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super("INVALID_TOKEN", message, cause);
    }
}
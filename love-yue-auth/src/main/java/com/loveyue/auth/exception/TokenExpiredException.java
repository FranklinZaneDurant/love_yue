package com.loveyue.auth.exception;

/**
 * 令牌过期异常
 * 
 * @author loveyue
 * @since 2025-07-13
 */
public class TokenExpiredException extends AuthException {

    public TokenExpiredException(String message) {
        super("TOKEN_EXPIRED", message);
    }

    public TokenExpiredException(String message, Object... args) {
        super("TOKEN_EXPIRED", message, args);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super("TOKEN_EXPIRED", message, cause);
    }
}
package com.loveyue.auth.exception;

/**
 * 登录失败异常
 * 
 * @author loveyue
 * @since 2025-07-13
 */
public class LoginFailedException extends AuthException {

    public LoginFailedException(String message) {
        super("LOGIN_FAILED", message);
    }

    public LoginFailedException(String message, Object... args) {
        super("LOGIN_FAILED", message, args);
    }

    public LoginFailedException(String message, Throwable cause) {
        super("LOGIN_FAILED", message, cause);
    }
}
package com.loveyue.auth.exception;

/**
 * 用户不存在异常
 * 
 * @author loveyue
 * @since 2025-07-13
 */
public class UserNotFoundException extends AuthException {

    public UserNotFoundException(String message) {
        super("USER_NOT_FOUND", message);
    }

    public UserNotFoundException(String message, Object... args) {
        super("USER_NOT_FOUND", message, args);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super("USER_NOT_FOUND", message, cause);
    }
}
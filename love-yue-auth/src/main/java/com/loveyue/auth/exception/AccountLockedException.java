package com.loveyue.auth.exception;

/**
 * 账户锁定异常
 * 
 * @author loveyue
 * @since 2025-07-13
 */
public class AccountLockedException extends AuthException {

    public AccountLockedException(String message) {
        super("ACCOUNT_LOCKED", message);
    }

    public AccountLockedException(String message, Object... args) {
        super("ACCOUNT_LOCKED", message, args);
    }

    public AccountLockedException(String message, Throwable cause) {
        super("ACCOUNT_LOCKED", message, cause);
    }
}
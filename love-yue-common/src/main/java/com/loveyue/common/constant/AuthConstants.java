package com.loveyue.common.constant;

/**
 * @Description: 统一认证服务的常量定义，包含JWT、SSO、安全策略等相关常量
 * @Date 2025/7/31
 * @Author LoveYue
 */
public class AuthConstants {

    public static final String TOKEN_PREFIX = "Bearer";

    public static final String TOKEN_HEADER = "Authorization";

    public static final String TOKEN_TYPE = "JWT";

    public static final String JWT_ISSUER = "love-yue-auth";

    public static final String JWT_AUDIENCE_SYSTEM = "love_yue-system";

    public static final String JWT_CLAIM_USER_ID = "userId";

    public static final String JWT_CLAIM_USERNAME = "username";

    public static final String JWT_CLAIM_ROLES = "roles";

    public static final String JWT_CLAIM_AUTHORITIES = "authorities";

    public static final String JWT_CLAIM_TOKEN_TYPE = "tokenType";

    public static final String ACCESS_TOKEN_TYPE = "access_token";

    public static final String REFRESH_TOKEN_TYPE = "refresh_token";

    public static final String SSO_SESSION_COOKIE_NAME = "LOVE_YUE_SSO_SESSION";

    public static final String SSO_SESSION_HEADER = "X-SSO-Session";

    public static final String SSO_REDIRECT_PARAM = "redirect_uri";

    public static final String SSO_LOGIN_PAGE = "/auth/login";

    public static final String SSO_LOGOUT_PAGE = "/auth/logout";

    public static final int DEFAULT_PASSWORD_MIN_LENGTH = 8;

    public static final int DEFAULT_PASSWORD_MAX_LENGTH = 32;

    public static final int DEFAULT_LOGIN_ATTEMPT_LIMIT = 5;

    public static final int DEFAULT_LOCKOUT_DURATION_MINUTES = 5;

    public static final int DEFAULT_SESSION_TIMEOUT_HOURS = 8;

    public static final int DEFAULT_ACCESS_TOKEN_EXPIRE_MINUTES = 30;

    public static final int DEFAULT_REFRESH_TOKEN_EXPIRE_DAYS = 7;

    public static final String USER_STATUS_ACTIVE = "ACTIVE";

    public static final String USER_STATUS_LOCKED = "LOCKED";

    public static final String USER_STATUS_DISABLED = "DISABLED";

    public static final String USER_STATUS_PENDING = "PENDING";

    public static final String AUTH_PROVIDER_LOCAL = "LOCAL";

    public static final String AUTH_PROVIDER_LDAP = "LDAP";

    public static final String AUTH_PROVIDER_OAUTH2 = "OAUTH2";

    public static final String LOGIN_RESULT_SUCCESS = "SUCCESS";

    public static final String LOGIN_RESULT_INVALID_CREDENTIALS = "INVALID_CREDENTIALS";

    public static final String LOGIN_RESULT_USER_NOT_FOUND = "USER_NOT_FOUND";

    public static final String LOGIN_RESULT_ACCOUNT_LOCKED = "ACCOUNT_LOCKED";

    public static final String LOGIN_RESULT_ACCOUNT_DISABLED = "ACCOUNT_DISABLED";

    public static final String LOGIN_RESULT_ACCOUNT_PENDING = "ACCOUNT_PENDING";

    public static final String AUTH_API_BASE_PATH = "/api/auth";

    public static final String LOGIN_API_PATH = "/login";

    public static final String LOGOUT_API_PATH = "/logout";

    public static final String REFRESH_TOKEN_API_PATH = "/refresh";

    public static final String VALIDATE_TOKEN_API_PATH = "/validate";

    public static final String USER_INFO_API_PATH = "/userinfo";

    public static final String REQUEST_ATTR_CURRENT_USER = "currentUser";

    public static final String REQUEST_ATTR_USER_ID = "userId";

    public static final String REQUEST_ATTR_USERNAME = "username";

    public static final String REQUEST_ATTR_USER_ROLES = "userRoles";

    public static final String AUTH_CONFIG_PREFIX = "auth";

    public static final String SSO_CONFIG_PREFIX = "auth.sso";

    public static final String JWT_CONFIG_PREFIX = "auth.jwt";

    public static final String SECURITY_CONFIG_PREFIX = "auth.security";

    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{4,20}$";

    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public static final String PHONE_PATTERN = "^1[3-9]\\d{9}$";

    public static final String STRONG_PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$";

    private AuthConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

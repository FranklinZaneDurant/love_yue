package com.loveyue.common.constant;

/**
 * Redis键名常量类
 * @Description: 统一管理认证服务中使用的Redis键名，包含SSO会话、Token缓存、用户信息缓存等
 * @Date 2025/7/31
 * @Author LoveYue
 */
public class RedisKeys {

    /**
     * 认证服务Redis键前缀
     */
    public static final String AUTH_PREFIX = "love_yue:auth:";
    
    /**
     * SSO相关键前缀
     */
    public static final String SSO_PREFIX = AUTH_PREFIX + "sso:";
    
    /**
     * Token相关键前缀
     */
    public static final String TOKEN_PREFIX = AUTH_PREFIX + "token:";
    
    /**
     * 用户相关键前缀
     */
    public static final String USER_PREFIX = AUTH_PREFIX + "user:";
    
    /**
     * 安全相关键前缀
     */
    public static final String SECURITY_PREFIX = AUTH_PREFIX + "security:";
    
    /**
     * 缓存相关键前缀
     */
    public static final String CACHE_PREFIX = AUTH_PREFIX + "cache:";

    /**
     * SSO会话键模板：love_yue:auth:sso:session:{sessionId}
     * 存储SSO会话信息
     */
    public static final String SSO_SESSION = SSO_PREFIX + "session:";
    
    /**
     * 用户SSO会话映射键模板：love_yue:auth:sso:user_session:{userId}
     * 存储用户ID到SSO会话ID的映射
     */
    public static final String SSO_USER_SESSION = SSO_PREFIX + "user_session:";
    
    /**
     * SSO会话索引键模板：love_yue:auth:sso:session_index:{username}
     * 存储用户名到SSO会话ID的映射
     */
    public static final String SSO_SESSION_INDEX = SSO_PREFIX + "session_index:";
    
    /**
     * 活跃SSO会话集合：love_yue:auth:sso:active_sessions
     * 存储所有活跃的SSO会话ID
     */
    public static final String SSO_ACTIVE_SESSIONS = SSO_PREFIX + "active_sessions";

    /**
     * 访问Token黑名单键模板：love_yue:auth:token:blacklist:access:{tokenId}
     * 存储被撤销的访问Token
     */
    public static final String TOKEN_BLACKLIST_ACCESS = TOKEN_PREFIX + "blacklist:access:";
    
    /**
     * 刷新Token黑名单键模板：love_yue:auth:token:blacklist:refresh:{tokenId}
     * 存储被撤销的刷新Token
     */
    public static final String TOKEN_BLACKLIST_REFRESH = TOKEN_PREFIX + "blacklist:refresh:";
    
    /**
     * 用户Token映射键模板：love_yue:auth:token:user_tokens:{userId}
     * 存储用户的所有有效Token ID列表
     */
    public static final String TOKEN_USER_TOKENS = TOKEN_PREFIX + "user_tokens:";
    
    /**
     * 刷新Token存储键模板：love_yue:auth:token:refresh:{tokenId}
     * 存储刷新Token的详细信息
     */
    public static final String TOKEN_REFRESH_INFO = TOKEN_PREFIX + "refresh:";
    
    /**
     * Token统计信息键模板：love_yue:auth:token:stats:{userId}
     * 存储用户Token使用统计
     */
    public static final String TOKEN_STATS = TOKEN_PREFIX + "stats:";

    /**
     * 用户信息缓存键模板：love_yue:auth:cache:user_info:{userId}
     * 缓存用户基本信息
     */
    public static final String CACHE_USER_INFO = CACHE_PREFIX + "user_info:";
    
    /**
     * 用户权限缓存键模板：love_yue:auth:cache:user_permissions:{userId}
     * 缓存用户权限信息
     */
    public static final String CACHE_USER_PERMISSIONS = CACHE_PREFIX + "user_permissions:";
    
    /**
     * 用户角色缓存键模板：love_yue:auth:cache:user_roles:{userId}
     * 缓存用户角色信息
     */
    public static final String CACHE_USER_ROLES = CACHE_PREFIX + "user_roles:";
    
    /**
     * 用户名到用户ID映射缓存：love_yue:auth:cache:username_to_id:{username}
     * 缓存用户名到用户ID的映射关系
     */
    public static final String CACHE_USERNAME_TO_ID = CACHE_PREFIX + "username_to_id:";

    /**
     * 登录尝试记录键模板：love_yue:auth:security:login_attempts:{username}
     * 记录用户登录尝试次数
     */
    public static final String SECURITY_LOGIN_ATTEMPTS = SECURITY_PREFIX + "login_attempts:";
    
    /**
     * 账户锁定键模板：love_yue:auth:security:account_locked:{username}
     * 标记被锁定的账户
     */
    public static final String SECURITY_ACCOUNT_LOCKED = SECURITY_PREFIX + "account_locked:";
    
    /**
     * IP登录尝试记录键模板：love_yue:auth:security:ip_attempts:{ip}
     * 记录IP地址的登录尝试次数
     */
    public static final String SECURITY_IP_ATTEMPTS = SECURITY_PREFIX + "ip_attempts:";
    
    /**
     * IP黑名单键模板：love_yue:auth:security:ip_blacklist:{ip}
     * 存储被拉黑的IP地址
     */
    public static final String SECURITY_IP_BLACKLIST = SECURITY_PREFIX + "ip_blacklist:";
    
    /**
     * 验证码键模板：love_yue:auth:security:captcha:{sessionId}
     * 存储验证码信息
     */
    public static final String SECURITY_CAPTCHA = SECURITY_PREFIX + "captcha:";
    
    /**
     * 密码重置Token键模板：love_yue:auth:security:reset_token:{token}
     * 存储密码重置Token
     */
    public static final String SECURITY_RESET_TOKEN = SECURITY_PREFIX + "reset_token:";

    /**
     * API限流键模板：love_yue:auth:security:rate_limit:api:{api}:{identifier}
     * API接口限流
     */
    public static final String SECURITY_RATE_LIMIT_API = SECURITY_PREFIX + "rate_limit:api:";
    
    /**
     * 用户操作限流键模板：love_yue:auth:security:rate_limit:user:{userId}:{action}
     * 用户操作限流
     */
    public static final String SECURITY_RATE_LIMIT_USER = SECURITY_PREFIX + "rate_limit:user:";
    
    /**
     * IP限流键模板：love_yue:auth:security:rate_limit:ip:{ip}
     * IP地址限流
     */
    public static final String SECURITY_RATE_LIMIT_IP = SECURITY_PREFIX + "rate_limit:ip:";

    /**
     * 系统配置缓存：love_yue:auth:cache:system_config
     * 缓存系统配置信息
     */
    public static final String CACHE_SYSTEM_CONFIG = CACHE_PREFIX + "system_config";
    
    /**
     * 认证配置缓存：love_yue:auth:cache:auth_config
     * 缓存认证相关配置
     */
    public static final String CACHE_AUTH_CONFIG = CACHE_PREFIX + "auth_config";
    
    /**
     * JWT配置缓存：love_yue:auth:cache:jwt_config
     * 缓存JWT相关配置
     */
    public static final String CACHE_JWT_CONFIG = CACHE_PREFIX + "jwt_config";

    /**
     * 在线用户统计：love_yue:auth:stats:online_users
     * 统计当前在线用户数量
     */
    public static final String STATS_ONLINE_USERS = AUTH_PREFIX + "stats:online_users";
    
    /**
     * 登录统计键模板：love_yue:auth:stats:login:{date}
     * 按日期统计登录次数
     */
    public static final String STATS_LOGIN_COUNT = AUTH_PREFIX + "stats:login:";
    
    /**
     * 用户活跃度统计键模板：love_yue:auth:stats:user_activity:{userId}:{date}
     * 用户活跃度统计
     */
    public static final String STATS_USER_ACTIVITY = AUTH_PREFIX + "stats:user_activity:";

    /**
     * 构建SSO会话键
     * @param sessionId SSO会话ID
     * @return 完整的Redis键名
     */
    public static String buildSSOSessionKey(String sessionId) {
        return SSO_SESSION + sessionId;
    }
    
    /**
     * 构建用户SSO会话映射键
     * @param userId 用户ID
     * @return 完整的Redis键名
     */
    public static String buildSSOUserSessionKey(String userId) {
        return SSO_USER_SESSION + userId;
    }
    
    /**
     * 构建SSO会话索引键
     * @param username 用户名
     * @return 完整的Redis键名
     */
    public static String buildSSOSessionIndexKey(String username) {
        return SSO_SESSION_INDEX + username;
    }
    
    /**
     * 构建Token黑名单键
     * @param tokenId Token ID
     * @param tokenType Token类型（access/refresh）
     * @return 完整的Redis键名
     */
    public static String buildTokenBlacklistKey(String tokenId, String tokenType) {
        if (AuthConstants.ACCESS_TOKEN_TYPE.equals(tokenType)) {
            return TOKEN_BLACKLIST_ACCESS + tokenId;
        } else if (AuthConstants.REFRESH_TOKEN_TYPE.equals(tokenType)) {
            return TOKEN_BLACKLIST_REFRESH + tokenId;
        }
        throw new IllegalArgumentException("Invalid token type: " + tokenType);
    }
    
    /**
     * 构建用户Token映射键
     * @param userId 用户ID
     * @return 完整的Redis键名
     */
    public static String buildUserTokensKey(String userId) {
        return TOKEN_USER_TOKENS + userId;
    }
    
    /**
     * 构建刷新Token信息键
     * @param tokenId Token ID
     * @return 完整的Redis键名
     */
    public static String buildRefreshTokenInfoKey(String tokenId) {
        return TOKEN_REFRESH_INFO + tokenId;
    }
    
    /**
     * 构建用户信息缓存键
     * @param userId 用户ID
     * @return 完整的Redis键名
     */
    public static String buildUserInfoCacheKey(String userId) {
        return CACHE_USER_INFO + userId;
    }
    
    /**
     * 构建用户权限缓存键
     * @param userId 用户ID
     * @return 完整的Redis键名
     */
    public static String buildUserPermissionsCacheKey(String userId) {
        return CACHE_USER_PERMISSIONS + userId;
    }
    
    /**
     * 构建用户角色缓存键
     * @param userId 用户ID
     * @return 完整的Redis键名
     */
    public static String buildUserRolesCacheKey(String userId) {
        return CACHE_USER_ROLES + userId;
    }
    
    /**
     * 构建用户名到ID映射缓存键
     * @param username 用户名
     * @return 完整的Redis键名
     */
    public static String buildUsernameToIdCacheKey(String username) {
        return CACHE_USERNAME_TO_ID + username;
    }
    
    /**
     * 构建登录尝试记录键
     * @param username 用户名
     * @return 完整的Redis键名
     */
    public static String buildLoginAttemptsKey(String username) {
        return SECURITY_LOGIN_ATTEMPTS + username;
    }
    
    /**
     * 构建账户锁定键
     * @param username 用户名
     * @return 完整的Redis键名
     */
    public static String buildAccountLockedKey(String username) {
        return SECURITY_ACCOUNT_LOCKED + username;
    }
    
    /**
     * 构建IP登录尝试记录键
     * @param ip IP地址
     * @return 完整的Redis键名
     */
    public static String buildIpAttemptsKey(String ip) {
        return SECURITY_IP_ATTEMPTS + ip;
    }
    
    /**
     * 构建IP黑名单键
     * @param ip IP地址
     * @return 完整的Redis键名
     */
    public static String buildIpBlacklistKey(String ip) {
        return SECURITY_IP_BLACKLIST + ip;
    }
    
    /**
     * 构建验证码键
     * @param sessionId 会话ID
     * @return 完整的Redis键名
     */
    public static String buildCaptchaKey(String sessionId) {
        return SECURITY_CAPTCHA + sessionId;
    }
    
    /**
     * 构建密码重置Token键
     * @param token 重置Token
     * @return 完整的Redis键名
     */
    public static String buildResetTokenKey(String token) {
        return SECURITY_RESET_TOKEN + token;
    }
    
    /**
     * 构建API限流键
     * @param api API标识
     * @param identifier 标识符（用户ID、IP等）
     * @return 完整的Redis键名
     */
    public static String buildApiRateLimitKey(String api, String identifier) {
        return SECURITY_RATE_LIMIT_API + api + ":" + identifier;
    }
    
    /**
     * 构建用户操作限流键
     * @param userId 用户ID
     * @param action 操作类型
     * @return 完整的Redis键名
     */
    public static String buildUserRateLimitKey(String userId, String action) {
        return SECURITY_RATE_LIMIT_USER + userId + ":" + action;
    }
    
    /**
     * 构建IP限流键
     * @param ip IP地址
     * @return 完整的Redis键名
     */
    public static String buildIpRateLimitKey(String ip) {
        return SECURITY_RATE_LIMIT_IP + ip;
    }
    
    /**
     * 构建登录统计键
     * @param date 日期（格式：yyyy-MM-dd）
     * @return 完整的Redis键名
     */
    public static String buildLoginStatsKey(String date) {
        return STATS_LOGIN_COUNT + date;
    }
    
    /**
     * 构建用户活跃度统计键
     * @param userId 用户ID
     * @param date 日期（格式：yyyy-MM-dd）
     * @return 完整的Redis键名
     */
    public static String buildUserActivityStatsKey(String userId, String date) {
        return STATS_USER_ACTIVITY + userId + ":" + date;
    }
    
    /**
     * 私有构造函数，防止实例化
     */
    private RedisKeys() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

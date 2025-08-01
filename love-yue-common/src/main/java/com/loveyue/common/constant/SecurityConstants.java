package com.loveyue.common.constant;

/**
 * 安全相关常量类
 *
 * @Description: 统一管理认证服务中的安全策略常量，包含加密算法、权限控制、安全策略等
 * @Date 2025/7/31
 * @Author LoveYue
 */
public class SecurityConstants {

    /**
     * 默认密码加密算法
     */
    public static final String DEFAULT_PASSWORD_ENCODER = "BCrypt";

    /**
     * BCrypt加密强度
     */
    public static final int BCRYPT_STRENGTH = 12;

    /**
     * AES加密算法
     */
    public static final String AES_ALGORITHM = "AES";

    /**
     * AES/CBC/PKCS5Padding加密模式
     */
    public static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    /**
     * RSA加密算法
     */
    public static final String RSA_ALGORITHM = "RSA";

    /**
     * RSA密钥长度
     */
    public static final int RSA_KEY_SIZE = 2048;

    /**
     * SHA-256哈希算法
     */
    public static final String SHA256_ALGORITHM = "SHA-256";

    /**
     * MD5哈希算法（不推荐用于密码）
     */
    public static final String MD5_ALGORITHM = "MD5";

    /**
     * HMAC-SHA256算法
     */
    public static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    /**
     * 密码最小长度
     */
    public static final int PASSWORD_MIN_LENGTH = 8;

    /**
     * 密码最大长度
     */
    public static final int PASSWORD_MAX_LENGTH = 32;

    /**
     * 密码必须包含小写字母
     */
    public static final boolean PASSWORD_REQUIRE_LOWERCASE = true;

    /**
     * 密码必须包含大写字母
     */
    public static final boolean PASSWORD_REQUIRE_UPPERCASE = true;

    /**
     * 密码必须包含数字
     */
    public static final boolean PASSWORD_REQUIRE_DIGIT = true;

    /**
     * 密码必须包含特殊字符
     */
    public static final boolean PASSWORD_REQUIRE_SPECIAL_CHAR = true;

    /**
     * 密码特殊字符集合
     */
    public static final String PASSWORD_SPECIAL_CHARS = "@$!%*?&";

    /**
     * 密码历史记录数量（防止重复使用）
     */
    public static final int PASSWORD_HISTORY_COUNT = 5;

    /**
     * 密码过期天数
     */
    public static final int PASSWORD_EXPIRE_DAYS = 90;

    /**
     * 密码过期提醒天数
     */
    public static final int PASSWORD_EXPIRE_WARNING_DAYS = 7;

    /**
     * 最大登录尝试次数
     */
    public static final int MAX_LOGIN_ATTEMPTS = 5;

    /**
     * 账户锁定时长（分钟）
     */
    public static final int ACCOUNT_LOCKOUT_DURATION_MINUTES = 5;

    /**
     * IP锁定时长（分钟）
     */
    public static final int IP_LOCKOUT_DURATION_MINUTES = 60;

    /**
     * 登录失败重置时间窗口（分钟）
     */
    public static final int LOGIN_ATTEMPT_RESET_WINDOW_MINUTES = 15;

    /**
     * 同一IP最大并发登录尝试次数
     */
    public static final int MAX_CONCURRENT_LOGIN_ATTEMPTS_PER_IP = 10;

    /**
     * 用户最大并发会话数
     */
    public static final int MAX_CONCURRENT_SESSIONS_PER_USER = 3;

    /**
     * 验证码有效期（分钟）
     */
    public static final int CAPTCHA_EXPIRE_MINUTES = 5;

    /**
     * 验证码长度
     */
    public static final int CAPTCHA_LENGTH = 4;

    /**
     * 默认会话超时时间（小时）
     */
    public static final int DEFAULT_SESSION_TIMEOUT_HOURS = 8;

    /**
     * 最大会话超时时间（小时）
     */
    public static final int MAX_SESSION_TIMEOUT_HOURS = 24;

    /**
     * 会话空闲超时时间（分钟）
     */
    public static final int SESSION_IDLE_TIMEOUT_MINUTES = 30;

    /**
     * 记住我功能的有效期（天）
     */
    public static final int REMEMBER_ME_EXPIRE_DAYS = 30;

    /**
     * 会话固化攻击防护：登录后重新生成会话ID
     */
    public static final boolean SESSION_FIXATION_PROTECTION = true;

    /**
     * JWT签名算法
     */
    public static final String JWT_SIGNATURE_ALGORITHM = "HS256";

    /**
     * JWT密钥最小长度（字节）
     */
    public static final int JWT_SECRET_MIN_LENGTH = 32;

    /**
     * Access Token默认过期时间（分钟）
     */
    public static final int ACCESS_TOKEN_DEFAULT_EXPIRE_MINUTES = 30;

    /**
     * Refresh Token默认过期时间（天）
     */
    public static final int REFRESH_TOKEN_DEFAULT_EXPIRE_DAYS = 7;

    /**
     * Token刷新提前时间（分钟）
     */
    public static final int TOKEN_REFRESH_ADVANCE_MINUTES = 5;

    /**
     * Token黑名单缓存时间（小时）
     */
    public static final int TOKEN_BLACKLIST_CACHE_HOURS = 24;

    /**
     * 超级管理员角色
     */
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";

    /**
     * 系统管理员角色
     */
    public static final String ROLE_ADMIN = "ADMIN";

    /**
     * 普通用户角色
     */
    public static final String ROLE_USER = "USER";

    /**
     * 访客角色
     */
    public static final String ROLE_GUEST = "GUEST";

    /**
     * 权限前缀
     */
    public static final String AUTHORITY_PREFIX = "PERM_";

    /**
     * 角色前缀
     */
    public static final String ROLE_PREFIX = "ROLE_";

    /**
     * 用户管理权限
     */
    public static final String PERM_USER_MANAGE = "USER_MANAGE";

    /**
     * 系统配置权限
     */
    public static final String PERM_SYSTEM_CONFIG = "SYSTEM_CONFIG";

    /**
     * 日志查看权限
     */
    public static final String PERM_LOG_VIEW = "LOG_VIEW";

    /**
     * 监控查看权限
     */
    public static final String PERM_MONITOR_VIEW = "MONITOR_VIEW";

    /**
     * API默认限流次数（每分钟）
     */
    public static final int API_RATE_LIMIT_PER_MINUTE = 60;

    /**
     * 登录API限流次数（每分钟）
     */
    public static final int LOGIN_API_RATE_LIMIT_PER_MINUTE = 10;

    /**
     * 注册API限流次数（每小时）
     */
    public static final int REGISTER_API_RATE_LIMIT_PER_HOUR = 5;

    /**
     * 密码重置API限流次数（每小时）
     */
    public static final int PASSWORD_RESET_API_RATE_LIMIT_PER_HOUR = 3;

    /**
     * 验证码API限流次数（每分钟）
     */
    public static final int CAPTCHA_API_RATE_LIMIT_PER_MINUTE = 10;

    /**
     * 单个IP每分钟最大请求数
     */
    public static final int IP_RATE_LIMIT_PER_MINUTE = 100;

    /**
     * 单个用户每分钟最大请求数
     */
    public static final int USER_RATE_LIMIT_PER_MINUTE = 200;

    /**
     * X-Frame-Options头值
     */
    public static final String X_FRAME_OPTIONS = "DENY";

    /**
     * X-Content-Type-Options头值
     */
    public static final String X_CONTENT_TYPE_OPTIONS = "nosniff";

    /**
     * X-XSS-Protection头值
     */
    public static final String X_XSS_PROTECTION = "1; mode=block";

    /**
     * Strict-Transport-Security头值
     */
    public static final String STRICT_TRANSPORT_SECURITY = "max-age=31536000; includeSubDomains";

    /**
     * Content-Security-Policy头值
     */
    public static final String CONTENT_SECURITY_POLICY = "default-src 'self'";

    /**
     * Referrer-Policy头值
     */
    public static final String REFERRER_POLICY = "strict-origin-when-cross-origin";

    /**
     * 登录成功事件
     */
    public static final String AUDIT_LOGIN_SUCCESS = "LOGIN_SUCCESS";

    /**
     * 登录失败事件
     */
    public static final String AUDIT_LOGIN_FAILURE = "LOGIN_FAILURE";

    /**
     * 登出事件
     */
    public static final String AUDIT_LOGOUT = "LOGOUT";

    /**
     * 密码修改事件
     */
    public static final String AUDIT_PASSWORD_CHANGE = "PASSWORD_CHANGE";

    /**
     * 账户锁定事件
     */
    public static final String AUDIT_ACCOUNT_LOCKED = "ACCOUNT_LOCKED";

    /**
     * 账户解锁事件
     */
    public static final String AUDIT_ACCOUNT_UNLOCKED = "ACCOUNT_UNLOCKED";

    /**
     * 权限变更事件
     */
    public static final String AUDIT_PERMISSION_CHANGE = "PERMISSION_CHANGE";

    /**
     * 敏感操作事件
     */
    public static final String AUDIT_SENSITIVE_OPERATION = "SENSITIVE_OPERATION";

    /**
     * 访问拒绝事件
     */
    public static final String AUDIT_EVENT_ACCESS_DENIED = "ACCESS_DENIED";

    /**
     * 手机号脱敏正则
     */
    public static final String PHONE_MASK_REGEX = "(\\d{3})\\d{4}(\\d{4})";

    /**
     * 手机号脱敏替换
     */
    public static final String PHONE_MASK_REPLACEMENT = "$1****$2";

    /**
     * 邮箱脱敏正则
     */
    public static final String EMAIL_MASK_REGEX = "(\\w{1,3})\\w*@(\\w+)";

    /**
     * 邮箱脱敏替换
     */
    public static final String EMAIL_MASK_REPLACEMENT = "$1***@$2";

    /**
     * 身份证脱敏正则
     */
    public static final String ID_CARD_MASK_REGEX = "(\\d{4})\\d{10}(\\d{4})";

    /**
     * 身份证脱敏替换
     */
    public static final String ID_CARD_MASK_REPLACEMENT = "$1**********$2";

    /**
     * 是否启用CSRF保护
     */
    public static final boolean CSRF_PROTECTION_ENABLED = true;

    /**
     * 是否启用CORS
     */
    public static final boolean CORS_ENABLED = true;

    /**
     * 允许的CORS源
     */
    public static final String[] CORS_ALLOWED_ORIGINS = {};

    /**
     * 允许的CORS方法
     */
    public static final String[] CORS_ALLOWED_METHODS = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};

    /**
     * 允许的CORS头
     */
    public static final String[] CORS_ALLOWED_HEADERS = {"*"};

    /**
     * CORS预检请求缓存时间（秒）
     */
    public static final long CORS_MAX_AGE = 3600;

    /**
     * 默认AES密钥（生产环境应从配置文件读取）
     */
    public static final String DEFAULT_AES_KEY = "LoveYueAuth2025!";

    /**
     * 默认AES初始化向量
     */
    public static final String DEFAULT_AES_IV = "LoveYueIV2025!!";

    /**
     * 盐值长度
     */
    public static final int SALT_LENGTH = 16;

    /**
     * 随机数生成器算法
     */
    public static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";

    /**
     * 获取完整的权限名称
     *
     * @param permission 权限名称
     * @return 带前缀的权限名称
     */
    public static String getFullPermission(String permission) {
        return AUTHORITY_PREFIX + permission;
    }

    /**
     * 获取完整的角色名称
     *
     * @param role 角色名称
     * @return 带前缀的角色名称
     */
    public static String getFullRole(String role) {
        return ROLE_PREFIX + role;
    }

    /**
     * 检查是否为管理员角色
     *
     * @param role 角色名称
     * @return 是否为管理员角色
     */
    public static boolean isAdminRole(String role) {
        return ROLE_SUPER_ADMIN.equals(role) || ROLE_ADMIN.equals(role);
    }

    /**
     * 获取密码强度要求描述
     *
     * @return 密码强度要求描述
     */
    public static String getPasswordPolicyDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("密码长度：").append(PASSWORD_MIN_LENGTH).append("-").append(PASSWORD_MAX_LENGTH).append("位；");
        if (PASSWORD_REQUIRE_LOWERCASE) {
            sb.append("必须包含小写字母；");
        }
        if (PASSWORD_REQUIRE_UPPERCASE) {
            sb.append("必须包含大写字母；");
        }
        if (PASSWORD_REQUIRE_DIGIT) {
            sb.append("必须包含数字；");
        }
        if (PASSWORD_REQUIRE_SPECIAL_CHAR) {
            sb.append("必须包含特殊字符(").append(PASSWORD_SPECIAL_CHARS).append(")；");
        }
        return sb.toString();
    }

    /**
     * 私有构造函数，防止实例化
     */
    private SecurityConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

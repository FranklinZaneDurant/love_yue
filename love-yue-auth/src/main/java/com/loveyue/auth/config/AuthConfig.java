package com.loveyue.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 认证配置类
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "auth")
public class AuthConfig {

    /**
     * JWT配置
     */
    private Jwt jwt = new Jwt();

    /**
     * 登录配置
     */
    private Login login = new Login();

    /**
     * 令牌配置
     */
    private Token token = new Token();

    /**
     * 密码配置
     */
    private Password password = new Password();

    /**
     * 安全配置
     */
    private Security security = new Security();

    @Data
    public static class Jwt {
        /**
         * JWT密钥
         */
        private String secret = "loveyue-auth-jwt-secret-key-2025";

        /**
         * 访问令牌过期时间（分钟）
         */
        private int accessTokenExpireMinutes = 120;

        /**
         * 刷新令牌过期时间（天）
         */
        private int refreshTokenExpireDays = 7;

        /**
         * 临时令牌过期时间（分钟）
         */
        private int tempTokenExpireMinutes = 30;

        /**
         * 发行者
         */
        private String issuer = "loveyue-auth";

        /**
         * 受众
         */
        private String audience = "loveyue-app";

        /**
         * 是否启用JWT
         */
        private boolean enabled = true;
    }

    @Data
    public static class Login {
        /**
         * 最大登录尝试次数
         */
        private int maxAttempts = 5;

        /**
         * 账户锁定持续时间（分钟）
         */
        private int lockDurationMinutes = 30;

        /**
         * 是否允许多设备登录
         */
        private boolean allowMultipleDevices = true;

        /**
         * 单点登录检查
         */
        private boolean singleSignOnCheck = false;

        /**
         * 是否记录登录日志
         */
        private boolean logEnabled = true;

        /**
         * 是否启用验证码
         */
        private boolean captchaEnabled = false;

        /**
         * 验证码失败次数阈值
         */
        private int captchaThreshold = 3;
    }

    @Data
    public static class Token {
        /**
         * 每个用户的最大活跃令牌数
         */
        private int maxActiveTokensPerUser = 5;

        /**
         * 令牌清理间隔（小时）
         */
        private int cleanupIntervalHours = 24;

        /**
         * 是否自动清理过期令牌
         */
        private boolean autoCleanup = true;

        /**
         * 令牌使用更新阈值（分钟）
         */
        private int usageUpdateThresholdMinutes = 5;

        /**
         * 长时间未使用令牌阈值（天）
         */
        private int unusedTokenThresholdDays = 30;
    }

    @Data
    public static class Password {
        /**
         * 密码过期天数
         */
        private int expireDays = 90;

        /**
         * 密码过期警告天数
         */
        private int expireWarningDays = 7;

        /**
         * 是否启用密码复杂度检查
         */
        private boolean complexityCheckEnabled = true;

        /**
         * 最小密码长度
         */
        private int minLength = 8;

        /**
         * 最大密码长度
         */
        private int maxLength = 32;

        /**
         * 是否需要包含数字
         */
        private boolean requireDigit = true;

        /**
         * 是否需要包含小写字母
         */
        private boolean requireLowercase = true;

        /**
         * 是否需要包含大写字母
         */
        private boolean requireUppercase = true;

        /**
         * 是否需要包含特殊字符
         */
        private boolean requireSpecialChar = true;

        /**
         * 密码重置令牌过期时间（分钟）
         */
        private int resetTokenExpireMinutes = 30;
    }

    @Data
    public static class Security {
        /**
         * 是否启用IP白名单
         */
        private boolean ipWhitelistEnabled = false;

        /**
         * IP白名单
         */
        private String[] ipWhitelist = {};

        /**
         * 是否启用设备指纹验证
         */
        private boolean deviceFingerprintEnabled = false;

        /**
         * 可疑登录检测
         */
        private boolean suspiciousLoginDetection = true;

        /**
         * 异地登录检测
         */
        private boolean remoteLocationDetection = true;

        /**
         * 异常时间登录检测
         */
        private boolean unusualTimeDetection = false;

        /**
         * 是否启用双因子认证
         */
        private boolean twoFactorAuthEnabled = false;

        /**
         * 会话超时时间（分钟）
         */
        private int sessionTimeoutMinutes = 480;

        /**
         * 是否启用CSRF保护
         */
        private boolean csrfProtectionEnabled = true;
    }
}
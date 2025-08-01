package com.loveyue.auth.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.loveyue.common.entity.BaseBusinessEntity;
import com.loveyue.common.enums.SessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Date;

/**
 * @Description: SSO会话实体类，用于管理单点登录的会话信息
 * @Date 2025/8/1
 * @Author LoveYue
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sso_session", indexes = {
        @Index(name = "idx_sso_user_id", columnList = "userId"),
        @Index(name = "idx_sso_username", columnList = "username"),
        @Index(name = "idx_sso_status", columnList = "sessionStatus"),
        @Index(name = "idx_sso_expires_at", columnList = "expiresAt"),
        @Index(name = "idx_sso_client_ip", columnList = "clientIp"),
        @Index(name = "idx_sso_device_id", columnList = "deviceId"),
        @Index(name = "idx_sso_last_activity", columnList = "lastActivityTime")
})
@Schema(description = "SSO会话实体")
public class SSOSession extends BaseBusinessEntity {
    @Serial
    private static final long serialVersionUID = -7800714394501670188L;

    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    @Schema(description = "用户ID")
    private Long userId;

    @NotBlank(message = "用户名不能为空")
    @Column(name = "username", nullable = false, length = 50)
    @Schema(description = "用户名")
    private String username;

    @NotNull(message = "会话状态不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "session_status", nullable = false, length = 20)
    @Schema(description = "会话状态")
    private SessionStatus sessionStatus;

    @NotNull(message = "会话过期时间不能为空")
    @Column(name = "expires_at", nullable = false)
    @Schema(description = "会话过期时间")
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private Date expiresAt;

    @Column(name = "last_activity_time")
    @Schema(description = "最后活动时间")
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private Date lastActivityTime;

    @Column(name = "client_ip", length = 45)
    @Schema(description = "客户端IP地址")
    private String clientIp;

    @Column(name = "user_agent", length = 500)
    @Schema(description = "用户代理信息")
    private String userAgent;

    @Column(name = "device_id", length = 128)
    @Schema(description = "设备唯一标识")
    private String deviceId;

    @Column(name = "device_type", length = 20)
    @Schema(description = "设备类型")
    private String deviceType;

    @Column(name = "browser_info", length = 200)
    @Schema(description = "浏览器信息")
    private String browserInfo;

    @Column(name = "os_info", length = 200)
    @Schema(description = "操作系统信息")
    private String osInfo;

    @Column(name = "location_info", length = 200)
    @Schema(description = "地理位置信息")
    private String locationInfo;

    @Column(name = "auth_method", length = 20)
    @Schema(description = "认证方式")
    private String authMethod;

    @Column(name = "mfa_used")
    @Schema(description = "是否使用多因子认证")
    private Boolean mfaUsed;

    @Column(name = "login_time")
    @Schema(description = "登录时间")
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private Date loginTime;

    @Column(name = "logout_time")
    @Schema(description = "登出时间")
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private Date logoutTime;

    @Column(name = "max_inactive_interval")
    @Schema(description = "最大非活动间隔（秒）")
    private Integer maxInactiveInterval;

    @Column(name = "access_count")
    @Schema(description = "访问次数")
    private Integer accessCount;

    @Column(name = "concurrent_sessions")
    @Schema(description = "并发会话数")
    private Integer concurrentSessions;

    @Column(name = "is_remember_me")
    @Schema(description = "是否记住我")
    private Boolean rememberMe;

    @Column(name = "authorized_apps", length = 1000)
    @Schema(description = "已授权的应用列表，JSON格式")
    private String authorizedApps;

    @Column(name = "permissions", length = 2000)
    @Schema(description = "用户权限信息，JSON格式")
    private String permissions;

    @Column(name = "roles", length = 500)
    @Schema(description = "用户角色信息，JSON格式")
    private String roles;

    @Column(name = "is_forced_logout")
    @Schema(description = "是否强制登出")
    private Boolean forcedLogout;

    @Column(name = "logout_reason", length = 100)
    @Schema(description = "登出原因")
    private String logoutReason;

    @Column(name = "risk_score")
    @Schema(description = "风险评分")
    private Integer riskScore;

    @Column(name = "is_suspicious")
    @Schema(description = "是否可疑会话")
    private Boolean suspicious;

    @Column(name = "security_level", length = 20)
    @Schema(description = "安全级别")
    private String securityLevel;

    /**
     * 检查会话是否已过期
     */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.before(new Date());
    }

    /**
     * 检查会话是否有效
     */
    public boolean isValid() {
        return SessionStatus.ACTIVE.equals(sessionStatus) && !isExpired();
    }

    /**
     * 检查会话是否非活跃
     */
    public boolean isInactive() {
        if (lastActivityTime == null || maxInactiveInterval == null) {
            return false;
        }
        long inactiveTime = (System.currentTimeMillis() - lastActivityTime.getTime()) / 1000;
        return inactiveTime > maxInactiveInterval;
    }

    /**
     * 更新最后活动时间
     */
    public void updateLastActivity() {
        this.lastActivityTime = new Date();
        if (this.accessCount == null) {
            this.accessCount = 1;
        } else {
            this.accessCount++;
        }
    }

    /**
     * 终止会话
     */
    public void terminate(String reason) {
        this.sessionStatus = SessionStatus.TERMINATED;
        this.logoutTime = new Date();
        this.logoutReason = reason;
    }

    /**
     * 强制登出
     */
    public void forceLogout(String reason) {
        this.sessionStatus = SessionStatus.FORCED_LOGOUT;
        this.forcedLogout = true;
        this.logoutTime = new Date();
        this.logoutReason = reason;
    }

    /**
     * 标记为可疑会话
     */
    public void markSuspicious(int riskScore, String reason) {
        this.sessionStatus = SessionStatus.SUSPICIOUS;
        this.suspicious = true;
        this.riskScore = riskScore;
        this.logoutReason = reason;
    }

    /**
     * 延长会话
     */
    public void extendSession(int extensionMinutes) {
        if (this.expiresAt != null) {
            long extensionMillis = extensionMinutes * 60 * 1000L;
            this.expiresAt = new Date(this.expiresAt.getTime() + extensionMillis);
        }
    }

    /**
     * 获取剩余有效时间（秒）
     */
    public long getRemainingTimeSeconds() {
        if (expiresAt == null) {
            return 0;
        }
        long remaining = (expiresAt.getTime() - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    /**
     * 设置设备信息
     */
    public void setDeviceInfo(String deviceId, String deviceType, String browserInfo, String osInfo) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.browserInfo = browserInfo;
        this.osInfo = osInfo;
    }

    /**
     * 设置认证信息
     */
    public void setAuthInfo(String authMethod, Boolean mfaUsed, String securityLevel) {
        this.authMethod = authMethod;
        this.mfaUsed = mfaUsed;
        this.securityLevel = securityLevel;
        this.loginTime = new Date();
    }

    /**
     * 设置会话配置
     */
    public void setSessionConfig(int maxInactiveInterval, boolean isRememberMe) {
        this.maxInactiveInterval = maxInactiveInterval;
        this.rememberMe = isRememberMe;

        if (isRememberMe) {
            this.expiresAt = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        } else {
            this.expiresAt = new Date(System.currentTimeMillis() + 8L * 60 * 60 * 1000);
        }
    }

    /**
     * 检查是否需要刷新会话
     */
    public boolean needsRefresh() {
        if (expiresAt == null) {
            return false;
        }
        long remainingMinutes = getRemainingTimeSeconds() / 60;
        return remainingMinutes < 30;
    }

    /**
     * 获取会话持续时间（分钟）
     */
    public long getSessionDurationMinutes() {
        if (loginTime == null) {
            return 0;
        }
        Date endTime = logoutTime != null ? logoutTime : new Date();
        return (endTime.getTime() - loginTime.getTime()) / (60 * 1000);
    }
}

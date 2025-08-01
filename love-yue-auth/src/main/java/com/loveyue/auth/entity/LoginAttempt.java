package com.loveyue.auth.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.loveyue.common.entity.BaseBusinessEntity;
import com.loveyue.common.enums.LoginResult;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.util.Date;

/**
 * @Description: 登录尝试记录实体类
 * @Date 2025/8/1
 * @Author LoveYue
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "login_attempt", indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_client_ip", columnList = "client_ip"),
        @Index(name = "idx_attempt_time", columnList = "attempt_time"),
        @Index(name = "idx_login_result", columnList = "login_result"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt extends BaseBusinessEntity {
    @Serial
    private static final long serialVersionUID = -6678017447848070905L;

    @NotBlank(message = "用户名不能为空")
    @Column(name = "username", nullable = false, length = 50)
    @Schema(name = "用户名")
    private String username;

    @Column(name = "user_id")
    @Schema(name = "用户ID（登录成功时记录）")
    private Long userId;

    @NotBlank(message = "客户端IP不能为空")
    @Column(name = "client_ip", nullable = false, length = 45)
    @Schema(name = "客户端IP地址")
    private String clientIp;

    @Column(name = "user_agent", length = 500)
    @Schema(name = "用户代理信息")
    private String userAgent;

    @NotNull(message = "尝试时间不能为空")
    @Column(name = "attempt_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "登录尝试时间")
    private Date attemptTime;

    @NotNull(message = "登录结果不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "login_result", nullable = false, length = 20)
    @Schema(name = "登录结果")
    private LoginResult loginResult;

    @Column(name = "failure_reason", length = 200)
    @Schema(name = "失败原因")
    private String failureReason;

    @Column(name = "device_id", length = 100)
    @Schema(name = "设备ID")
    private String deviceId;

    @Column(name = "device_type", length = 50)
    @Schema(name = "设备类型")
    private String deviceType;

    @Column(name = "browser_info", length = 200)
    @Schema(name = "浏览器信息")
    private String browserInfo;

    @Column(name = "os_info", length = 100)
    @Schema(name = "操作系统信息")
    private String osInfo;

    @Column(name = "location_info", length = 200)
    @Schema(name = "地理位置信息")
    private String locationInfo;

    @Column(name = "session_id", length = 100)
    @Schema(name = "会话ID（登录成功时记录）")
    private String sessionId;

    @Column(name = "access_token_id", length = 100)
    @Schema(name = "访问令牌ID（登录成功时记录）")
    private String accessTokenId;

    @Column(name = "login_duration")
    @Schema(name = "登录耗时（毫秒）")
    private Long loginDuration;

    @Column(name = "is_suspicious", nullable = false)
    @Builder.Default
    @Schema(name = "是否可疑登录")
    private Boolean suspicious = false;

    @Column(name = "risk_score")
    @Schema(name = "风险评分（0-100）")
    private Integer riskScore;

    @Column(name = "auth_method", length = 50)
    @Builder.Default
    @Schema(name = "认证方式")
    private String authMethod = "PASSWORD";

    @Column(name = "mfa_used", nullable = false)
    @Builder.Default
    @Schema(name = "是否使用多因子认证")
    private Boolean mfaUsed = false;

    @Column(name = "login_source", length = 50)
    @Schema(name = "登录来源")
    private String loginSource;

    @Column(name = "referrer_url", length = 500)
    @Schema(name = "来源页面URL")
    private String referrerUrl;

    @Column(name = "consecutive_failures")
    @Builder.Default
    @Schema(name = "连续失败次数")
    private Integer consecutiveFailures = 0;

    @Column(name = "blocked_by_policy", nullable = false)
    @Builder.Default
    @Schema(name = "是否被安全策略阻止")
    private Boolean blockedByPolicy = false;

    @Column(name = "policy_name", length = 100)
    @Schema(name = "触发的安全策略名称")
    private String policyName;

    @Column(name = "additional_info", columnDefinition = "TEXT")
    @Schema(name = "附加信息（JSON格式）")
    private String additionalInfo;

    /**
     * 检查是否为成功登录
     */
    public boolean isSuccessful() {
        return LoginResult.SUCCESS.equals(this.loginResult);
    }

    /**
     * 检查是否为失败登录
     */
    public boolean isFailure() {
        return !isSuccessful();
    }

    /**
     * 检查是否为可疑登录
     */
    public boolean isSuspiciousLogin() {
        return Boolean.TRUE.equals(this.suspicious) ||
               (this.riskScore != null && this.riskScore > 70);
    }

    /**
     * 设置登录成功信息
     */
    public void setSuccessInfo(Long userId, String sessionId, String accessTokenId) {
        this.loginResult = LoginResult.SUCCESS;
        this.userId = userId;
        this.sessionId = sessionId;
        this.accessTokenId = accessTokenId;
        this.consecutiveFailures = 0;
    }

    /**
     * 设置登录失败信息
     */
    public void setFailureInfo(LoginResult result, String reason, Integer consecutiveFailures) {
        this.loginResult = result;
        this.failureReason = reason;
        this.consecutiveFailures = consecutiveFailures;
    }

    /**
     * 设置风险评估信息
     */
    public void setRiskInfo(Integer riskScore, Boolean isSuspicious, String policyName) {
        this.riskScore = riskScore;
        this.suspicious= isSuspicious;
        if (Boolean.TRUE.equals(isSuspicious)) {
            this.blockedByPolicy = true;
            this.policyName = policyName;
        }
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
     * 计算登录耗时
     */
    public void calculateDuration(Date startTime) {
        if (startTime != null && this.attemptTime != null) {
            this.loginDuration = this.attemptTime.getTime() - startTime.getTime();
        }
    }

    /**
     * 获取简化的登录结果描述
     */
    public String getSimpleResult() {
        return isSuccessful() ? "成功" : "失败";
    }

    /**
     * 获取详细的登录结果描述
     */
    public String getDetailedResult() {
        if (loginResult != null) {
            return loginResult.getDescription() +
                   (failureReason != null ? " - " + failureReason : "");
        }
        return "未知";
    }
}

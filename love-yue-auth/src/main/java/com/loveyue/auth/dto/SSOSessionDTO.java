package com.loveyue.auth.dto;

import com.loveyue.common.enums.SessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @Description: SSO会话DTO
 * @Date 2025/8/1
 * @Author LoveYue
 */
@Data
public class SSOSessionDTO {
    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "会话状态")
    private SessionStatus sessionStatus;

    @Schema(description = "会话过期时间")
    private Date expiresAt;

    @Schema(description = "最后活动时间")
    private Date lastActivityTime;

    @Schema(description = "客户端IP地址")
    private String clientIp;

    @Schema(description = "用户代理信息")
    private String userAgent;

    @Schema(description = "设备唯一标识")
    private String deviceId;

    @Schema(description = "设备类型")
    private String deviceType;

    @Schema(description = "浏览器信息")
    private String browserInfo;

    @Schema(description = "操作系统信息")
    private String osInfo;

    @Schema(description = "地理位置信息")
    private String locationInfo;

    @Schema(description = "认证方式")
    private String authMethod;

    @Schema(description = "是否使用多因子认证")
    private Boolean mfaUsed;

    @Schema(description = "登录时间")
    private Date loginTime;

    @Schema(description = "登出时间")
    private Date logoutTime;

    @Schema(description = "最大非活动间隔（秒）")
    private Integer maxInactiveInterval;

    @Schema(description = "访问次数")
    private Integer accessCount;

    @Schema(description = "并发会话数")
    private Integer concurrentSessions;

    @Schema(description = "是否记住我")
    private Boolean rememberMe;

    @Schema(description = "已授权的应用列表，JSON格式")
    private String authorizedApps;

    @Schema(description = "用户权限信息，JSON格式")
    private String permissions;

    @Schema(description = "用户角色信息，JSON格式")
    private String roles;

    @Schema(description = "是否强制登出")
    private Boolean forcedLogout;

    @Schema(description = "登出原因")
    private String logoutReason;

    @Schema(description = "风险评分")
    private Integer riskScore;

    @Schema(description = "是否可疑会话")
    private Boolean suspicious;

    @Schema(description = "安全级别")
    private String securityLevel;
}

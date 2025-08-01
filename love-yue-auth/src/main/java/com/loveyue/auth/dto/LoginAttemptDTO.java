package com.loveyue.auth.dto;

import com.loveyue.common.dto.BaseDTO;
import com.loveyue.common.enums.LoginResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Date;

/**
 * @Description: 登录尝试记录DTO
 * @Date 2025/8/1
 * @Author LoveYue
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginAttemptDTO extends BaseDTO {
    @Serial
    private static final long serialVersionUID = -3546827759466412485L;

    @Schema(name = "用户名")
    private String username;

    @Schema(name = "用户ID（登录成功时记录）")
    private Long userId;

    @Schema(name = "客户端IP地址")
    private String clientIp;

    @Schema(name = "用户代理信息")
    private String userAgent;

    @Schema(name = "登录尝试时间")
    private Date attemptTime;

    @Schema(name = "登录结果")
    private LoginResult loginResult;

    @Schema(name = "失败原因")
    private String failureReason;

    @Schema(name = "设备ID")
    private String deviceId;

    @Schema(name = "设备类型")
    private String deviceType;

    @Schema(name = "浏览器信息")
    private String browserInfo;

    @Schema(name = "操作系统信息")
    private String osInfo;

    @Schema(name = "地理位置信息")
    private String locationInfo;

    @Schema(name = "会话ID（登录成功时记录）")
    private String sessionId;

    @Schema(name = "访问令牌ID（登录成功时记录）")
    private String accessTokenId;

    @Schema(name = "登录耗时（毫秒）")
    private Long loginDuration;

    @Schema(name = "是否可疑登录")
    private Boolean suspicious;

    @Schema(name = "风险评分（0-100）")
    private Integer riskScore;

    @Schema(name = "认证方式")
    private String authMethod;

    @Schema(name = "是否使用多因子认证")
    private Boolean mfaUsed;

    @Schema(name = "登录来源")
    private String loginSource;

    @Schema(name = "来源页面URL")
    private String referrerUrl;

    @Schema(name = "连续失败次数")
    private Integer consecutiveFailures;

    @Schema(name = "是否被安全策略阻止")
    private Boolean blockedByPolicy;

    @Schema(name = "触发的安全策略名称")
    private String policyName;

    @Schema(name = "附加信息（JSON格式）")
    private String additionalInfo;
}

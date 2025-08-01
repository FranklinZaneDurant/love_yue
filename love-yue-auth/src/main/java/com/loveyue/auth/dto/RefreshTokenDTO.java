package com.loveyue.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 刷新令牌DTO
 * @Date 2025/8/1
 * @Author LoveYue
 */
@Data
public class RefreshTokenDTO {
    @Schema(name = "刷新令牌值")
    private String tokenValue;

    @Schema(name = "用户Id")
    private Long userId;

    @Schema(name = "用户名")
    private String username;

    @Schema(name = "过期时间")
    private Date expiresAt;

    @Schema(name = "设备ID")
    private String deviceId;

    @Schema(name = "设备类型")
    private String deviceType;

    @Schema(name = "客户端IP地址")
    private String clientIp;

    @Schema(name = "用户代理信息")
    private String userAgent;

    @Schema(name = "是否已撤销")
    private Boolean revoked;

    @Schema(name = "撤销时间")
    private Date revokedAt;

    @Schema(name = "撤销人")
    private String revokedBy;

    @Schema(name = "撤销原因")
    private String revokeReason;

    @Schema(name = "最后使用时间")
    private Date lastUsedAt;

    @Schema(name = "使用次数")
    private Integer useCount;

    @Schema(name = "会话ID（用于SSO）")
    private String sessionId;

    @Schema(name = "令牌作用域")
    private String scope;

    @Schema(name = "令牌族（用于令牌轮换）")
    private String tokenFamily;
}

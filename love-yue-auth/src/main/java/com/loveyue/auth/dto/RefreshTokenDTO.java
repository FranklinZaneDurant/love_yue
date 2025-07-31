package com.loveyue.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新令牌请求DTO
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Data
public class RefreshTokenDTO {

    /**
     * 刷新令牌
     */
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;

    /**
     * 设备ID（用于验证令牌归属）
     */
    private String deviceId;

    /**
     * 客户端版本
     */
    private String clientVersion;

    /**
     * 应用标识
     */
    private String appId;
}
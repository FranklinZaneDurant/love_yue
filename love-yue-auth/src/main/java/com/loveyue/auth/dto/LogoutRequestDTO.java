package com.loveyue.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * 登出请求DTO
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Data
public class LogoutRequestDTO {

    /**
     * 访问令牌
     */
    @NotBlank(message = "访问令牌不能为空")
    private String accessToken;

    /**
     * 刷新令牌（可选，如果提供则一并撤销）
     */
    private String refreshToken;

    /**
     * 设备ID（用于验证令牌归属）
     */
    private String deviceId;

    /**
     * 登出类型（NORMAL-正常登出, FORCE-强制登出, TIMEOUT-超时登出）
     */
    private String logoutType = "NORMAL";

    /**
     * 是否登出所有设备
     */
    private Boolean logoutAllDevices = false;

    /**
     * 登出原因
     */
    private String reason;
}
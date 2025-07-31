package com.loveyue.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 令牌验证响应
 * 
 * @author loveyue
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {

    /**
     * 令牌是否有效
     */
    private boolean valid;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 权限列表
     */
    private List<String> permissions;

    /**
     * 令牌类型
     */
    private String tokenType;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 令牌过期时间
     */
    private LocalDateTime expiration;

    /**
     * 令牌剩余有效时间（秒）
     */
    private long remainingTime;

    /**
     * 错误信息（当令牌无效时）
     */
    private String errorMessage;
}
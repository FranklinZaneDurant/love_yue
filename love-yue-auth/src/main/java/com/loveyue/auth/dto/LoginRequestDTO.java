package com.loveyue.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 登录请求DTO
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Data
public class LoginRequestDTO {

    /**
     * 用户名（可以是用户名、邮箱或手机号）
     */
    @NotBlank(message = "用户名不能为空")
    @Size(max = 100, message = "用户名长度不能超过100个字符")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(max = 128, message = "密码长度不能超过128个字符")
    private String password;

    /**
     * 设备ID（用于设备识别和单点登录控制）
     */
    private String deviceId;

    /**
     * 设备类型（如：WEB、MOBILE、DESKTOP等）
     */
    private String deviceType;

    /**
     * 设备名称（如：Chrome浏览器、iPhone等）
     */
    private String deviceName;

    /**
     * 是否记住登录状态（影响令牌过期时间）
     */
    private Boolean rememberMe = false;

    /**
     * 验证码（如果启用了验证码）
     */
    private String captcha;

    /**
     * 验证码标识
     */
    private String captchaId;

    /**
     * 双因子认证码（如果启用了2FA）
     */
    private String twoFactorCode;

    /**
     * 登录类型（如：PASSWORD、SMS、EMAIL等）
     */
    private String loginType = "PASSWORD";

    /**
     * 客户端版本
     */
    private String clientVersion;

    /**
     * 应用标识
     */
    private String appId;

    /**
     * 时区
     */
    private String timezone;

    /**
     * 语言
     */
    private String language;

    /**
     * 额外参数
     */
    private String extra;
}
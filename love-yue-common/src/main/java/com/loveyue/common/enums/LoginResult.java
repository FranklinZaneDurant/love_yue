package com.loveyue.common.enums;

import lombok.Getter;

/**
 * @Description: 登录结果枚举
 * @Date 2025/8/1
 * @Author LoveYue
 */
@Getter
public enum LoginResult {
    SUCCESS("成功", "登录成功"),
    FAILURE_INVALID_CREDENTIALS("失败-凭证无效", "用户名或密码错误"),
    FAILURE_ACCOUNT_LOCKED("失败-账户锁定", "账户被锁定"),
    FAILURE_ACCOUNT_DISABLED("失败-账户禁用", "账户被禁用"),
    FAILURE_ACCOUNT_EXPIRED("失败-账户过期", "账户已过期"),
    FAILURE_PASSWORD_EXPIRED("失败-密码过期", "密码已过期"),
    FAILURE_TOO_MANY_ATTEMPTS("失败-尝试次数过多", "登录尝试次数过多"),
    FAILURE_CAPTCHA_REQUIRED("失败-需要验证码", "需要验证码验证"),
    FAILURE_CAPTCHA_INVALID("失败-验证码错误", "验证码错误"),
    FAILURE_MFA_REQUIRED("失败-需要多因子认证", "需要多因子认证"),
    FAILURE_MFA_INVALID("失败-多因子认证失败", "多因子认证失败"),
    FAILURE_IP_BLOCKED("失败-IP被阻止", "IP地址被阻止"),
    FAILURE_DEVICE_BLOCKED("失败-设备被阻止", "设备被阻止"),
    FAILURE_TIME_RESTRICTED("失败-时间限制", "不在允许的登录时间范围内"),
    FAILURE_LOCATION_RESTRICTED("失败-地理位置限制", "不在允许的登录地理位置范围内"),
    FAILURE_SYSTEM_ERROR("失败-系统错误", "系统内部错误");

    private final String description;
    private final String message;

    LoginResult(String description, String message) {
        this.description = description;
        this.message = message;
    }
}

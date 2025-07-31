package com.loveyue.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 认证提供者枚举
 * @Date 2025/7/31
 * @Author LoveYue
 */
@Getter
@AllArgsConstructor
public enum AuthProvider {
    LOCAL("本地认证"),
    LDAP("LDAP认证"),
    OAUTH("OAuth认证"),
    SSO("单点登录");

    private final String description;
}

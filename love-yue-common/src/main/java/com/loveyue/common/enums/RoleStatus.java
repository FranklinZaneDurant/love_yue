package com.loveyue.common.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @Description: 角色状态枚举
 * @Date 2025/8/1
 * @Author LoveYue
 */
@Schema(description = "角色状态枚举")
@Getter
public enum RoleStatus {
    ACTIVE("启用", "角色正常可用"),
    DISABLED("禁用", "角色被禁用"),
    PENDING("待审批", "角色等待审批"),
    EXPIRED("已过期", "角色已过期"),
    LOCKED("已锁定", "角色被锁定");

    private final String displayName;
    private final String description;

    RoleStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

}
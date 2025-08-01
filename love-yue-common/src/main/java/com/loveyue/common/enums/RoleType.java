package com.loveyue.common.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @Description: 角色类型枚举
 * @Date 2025/8/1
 * @Author LoveYue
 */
@Schema(description = "角色类型枚举")
@Getter
public enum RoleType {
    SYSTEM("系统角色", "系统内置角色，不可删除"),
    BUSINESS("业务角色", "业务相关角色"),
    CUSTOM("自定义角色", "用户自定义角色"),
    TEMPORARY("临时角色", "临时授权角色");

    private final String displayName;
    private final String description;

    RoleType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

}
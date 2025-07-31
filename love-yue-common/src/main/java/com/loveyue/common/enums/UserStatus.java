package com.loveyue.common.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @Description: 用户状态枚举类
 * @Date 2025/6/25
 * @Author LoveYue
 */
@Getter
@Schema(description = "用户状态枚举类")
public enum UserStatus {
    ACTIVE("正常", "用户可正常使用系统功能"),
    DISABLED("禁用", "管理员手动限制、违规操作"),
    DELETED("注销", "用户账户被主动删除、用户长时间未登录删除"),
    LOCKED("锁定", "多次输错密码自动锁定");

    private final String displayName;

    private final String description;

    UserStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}

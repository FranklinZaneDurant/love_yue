package com.loveyue.common.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @Description: 会话状态枚举
 * @Date 2025/8/1
 * @Author LoveYue
 */
@Schema(description = "会话状态枚举")
@Getter
public enum SessionStatus {
    ACTIVE("活跃", "会话正常活跃状态"),
    INACTIVE("非活跃", "会话超时未活动"),
    EXPIRED("已过期", "会话已过期"),
    TERMINATED("已终止", "会话被主动终止"),
    FORCED_LOGOUT("强制登出", "管理员强制登出"),
    SUSPICIOUS("可疑", "检测到可疑活动"),
    LOCKED("已锁定", "会话被锁定");

    private final String displayName;
    private final String description;

    SessionStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

}

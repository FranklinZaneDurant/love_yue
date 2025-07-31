package com.loveyue.common.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @Description: 实体状态类型枚举类
 * @Date 2025/6/23
 * @Author LoveYue
 */
@Getter
@Schema(description = "实体状态类型枚举类")
public enum EntityStatusEnum {
    EFFECTIVE("有效的"),
    DELETED("已删除的");

    private final String displayName;

    EntityStatusEnum(String displayName) {
        this.displayName = displayName;
    }
}

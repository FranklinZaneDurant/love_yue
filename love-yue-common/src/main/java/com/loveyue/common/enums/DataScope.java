package com.loveyue.common.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @Description: 数据权限范围枚举
 * @Date 2025/8/1
 * @Author LoveYue
 */
@Schema(description = "数据权限范围枚举")
@Getter
public enum DataScope {
    ALL("全部数据权限"),
    CUSTOM("自定义数据权限"),
    DEPT("部门数据权限"),
    DEPT_AND_CHILD("部门及以下数据权限"),
    SELF("仅本人数据权限");

    private final String description;

    DataScope(String description) {
        this.description = description;
    }

}
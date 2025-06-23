package enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @Description: 实体状态类型枚举类
 * @Date 2025/6/23
 * @Author LoveYue
 */
@Getter
public enum EntityStatusEnum {
    EFFECTIVE("有效的"),
    INVALID("无效的"),
    DELETED("已删除");

    private final String displayName;

    EntityStatusEnum(String displayName) {
        this.displayName = displayName;
    }
}

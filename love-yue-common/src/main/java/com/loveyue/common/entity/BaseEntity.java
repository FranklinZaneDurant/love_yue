package com.loveyue.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;
import com.loveyue.common.uitls.CryptoUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description: 数据实体类基类
 * @Date 2025/6/23
 * @Author LoveYue
 */
@Data
public abstract class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 5908489787919929004L;

    @Schema(description = "实体Id", type = "Long", requiredMode = Schema.RequiredMode.REQUIRED)
    @Id
    @Column(nullable = false)
    private Long id;

    protected BaseEntity() {
        this(generateId());
    }

    protected BaseEntity(Long id) {
        this.setId(id == null ? generateId() : id);
    }

    public static Long generateId() {
        return CryptoUtils.uniqueDecId();
    }
}

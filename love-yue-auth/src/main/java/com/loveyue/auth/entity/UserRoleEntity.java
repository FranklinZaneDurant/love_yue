package com.loveyue.auth.entity;

import com.loveyue.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 用户角色关联实体类
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_role_entity", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_role_id", columnList = "role_id"),
        @Index(name = "idx_user_role", columnList = "user_id,role_id", unique = true)
})
@Comment("用户角色关联表")
public class UserRoleEntity extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "user_id", nullable = false)
    @Comment("用户ID")
    private Long userId;

    @Column(name = "role_id", nullable = false)
    @Comment("角色ID")
    private Long roleId;

    @Column(name = "granted_by")
    @Comment("授权人ID")
    private Long grantedBy;

    @Column(name = "granted_time")
    @Comment("授权时间")
    private LocalDateTime grantedTime;

    @Column(name = "expire_time")
    @Comment("过期时间")
    private LocalDateTime expireTime;

    @Column(name = "remark", length = 500)
    @Comment("备注")
    private String remark;

    /**
     * 检查角色是否过期
     */
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }
}
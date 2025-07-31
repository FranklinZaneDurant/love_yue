package com.loveyue.auth.entity;

import com.loveyue.common.entity.BaseBusinessEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import java.io.Serial;

/**
 * 角色实体类
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "role_entity", indexes = {
        @Index(name = "idx_role_code", columnList = "role_code", unique = true)
})
@Comment("系统角色表")
public class RoleEntity extends BaseBusinessEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "role_code", nullable = false, length = 50)
    @Comment("角色编码")
    private String roleCode;

    @Column(name = "role_name", nullable = false, length = 100)
    @Comment("角色名称")
    private String roleName;

    @Column(name = "role_desc", length = 500)
    @Comment("角色描述")
    private String roleDesc;

    @Column(name = "sort_order")
    @Comment("排序")
    private Integer sortOrder = 0;

    @Column(name = "is_system")
    @Comment("是否系统角色")
    private Boolean isSystem = false;

    @Column(name = "remark", length = 500)
    @Comment("备注")
    private String remark;
}
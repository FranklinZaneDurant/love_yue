package com.loveyue.auth.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.loveyue.common.entity.BaseBusinessEntity;
import com.loveyue.common.enums.RoleStatus;
import com.loveyue.common.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Date;

/**
 * @Description: 角色实体类，用于管理系统角色和权限信息
 * @Date 2025/8/1
 * @Author LoveYue
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "role_entity", indexes = {
        @Index(name = "idx_role_name", columnList = "roleName"),
        @Index(name = "idx_role_type", columnList = "roleType"),
        @Index(name = "idx_role_status", columnList = "roleStatus"),
        @Index(name = "idx_role_level", columnList = "roleLevel"),
        @Index(name = "idx_role_parent_id", columnList = "parentId")
})
@Schema(description = "角色实体")
public class RoleEntity extends BaseBusinessEntity {
    @Serial
    private static final long serialVersionUID = -137115053694399678L;

    @NotBlank(message = "角色名称不能为空")
    @Column(name = "role_name", nullable = false, length = 100)
    @Schema(description = "角色名称")
    private String roleName;

    @Column(name = "role_name_en", length = 100)
    @Schema(description = "角色英文名称")
    private String roleNameEn;

    @Column(name = "description", length = 500)
    @Schema(description = "角色描述")
    private String description;

    @NotNull(message = "角色类型不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false, length = 20)
    @Schema(description = "角色类型")
    private RoleType roleType;

    @NotNull(message = "角色状态不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "role_status", nullable = false, length = 20)
    @Schema(description = "角色状态")
    private RoleStatus roleStatus;

    @Column(name = "parent_id")
    @Schema(description = "父角色ID，支持角色层级结构")
    private Long parentId;

    @Column(name = "role_level")
    @Schema(description = "角色层级，数字越小权限越高")
    private Integer roleLevel;

    @Column(name = "role_path", length = 500)
    @Schema(description = "角色路径，用于快速查找父子关系")
    private String rolePath;

    @Column(name = "sort_order")
    @Schema(description = "排序顺序")
    private Integer sortOrder;

    @Column(name = "permissions", length = 2000)
    @Schema(description = "角色权限列表，JSON格式存储")
    private String permissions;

    @Column(name = "data_scope", length = 20)
    @Schema(description = "数据权限范围")
    private String dataScope;

    @Column(name = "dept_ids", length = 500)
    @Schema(description = "部门权限范围，JSON格式存储部门ID列表")
    private String deptIds;

    @Column(name = "user_count")
    @Schema(description = "拥有此角色的用户数量")
    private Integer userCount;

    @Column(name = "is_default")
    @Schema(description = "是否为默认角色")
    private Boolean defaultRole;

    @Column(name = "is_system")
    @Schema(description = "是否为系统内置角色")
    private Boolean system;

    @Column(name = "effective_time")
    @Schema(description = "角色生效时间")
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private Date effectiveTime;

    @Column(name = "expiry_time")
    @Schema(description = "角色过期时间")
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private Date expiryTime;

    @Column(name = "approved_by")
    @Schema(description = "审批人ID")
    private Long approvedBy;

    @Column(name = "approved_time")
    @Schema(description = "审批时间")
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private Date approvedTime;

    @Column(name = "approval_reason", length = 500)
    @Schema(description = "审批原因")
    private String approvalReason;

    /**
     * 检查角色是否有效
     */
    public boolean isValid() {
        return RoleStatus.ACTIVE.equals(roleStatus) && !isExpired();
    }

    /**
     * 检查角色是否已过期
     */
    public boolean isExpired() {
        return expiryTime != null && expiryTime.before(new Date());
    }

    /**
     * 检查是否为系统角色
     */
    public boolean isSystemRole() {
        return Boolean.TRUE.equals(system) || RoleType.SYSTEM.equals(roleType);
    }

    /**
     * 检查是否为管理员角色
     */
    public boolean isAdminRole() {
        return roleType.equals(RoleType.SYSTEM);
    }

    /**
     * 检查是否可以删除
     */
    public boolean isDeletable() {
        return !isSystemRole() && !Boolean.TRUE.equals(defaultRole);
    }

    /**
     * 检查是否可以编辑
     */
    public boolean isEditable() {
        return !isSystemRole() || RoleStatus.ACTIVE.equals(roleStatus);
    }

    /**
     * 启用角色
     */
    public void enable() {
        this.roleStatus = RoleStatus.ACTIVE;
        if (this.effectiveTime == null) {
            this.effectiveTime = new Date();
        }
    }

    /**
     * 禁用角色
     */
    public void disable() {
        this.roleStatus = RoleStatus.DISABLED;
    }

    /**
     * 锁定角色
     */
    public void lock() {
        this.roleStatus = RoleStatus.LOCKED;
    }

    /**
     * 设置过期
     */
    public void expire() {
        this.roleStatus = RoleStatus.EXPIRED;
        if (this.expiryTime == null) {
            this.expiryTime = new Date();
        }
    }

    /**
     * 审批通过
     */
    public void approve(Long approverId, String reason) {
        this.roleStatus = RoleStatus.ACTIVE;
        this.approvedBy = approverId;
        this.approvedTime = new Date();
        this.approvalReason = reason;
        if (this.effectiveTime == null) {
            this.effectiveTime = new Date();
        }
    }

    /**
     * 审批拒绝
     */
    public void reject(Long approverId, String reason) {
        this.roleStatus = RoleStatus.DISABLED;
        this.approvedBy = approverId;
        this.approvedTime = new Date();
        this.approvalReason = reason;
    }

    /**
     * 更新用户数量
     */
    public void updateUserCount(int count) {
        this.userCount = Math.max(0, count);
    }

    /**
     * 增加用户数量
     */
    public void incrementUserCount() {
        this.userCount = (this.userCount == null ? 0 : this.userCount) + 1;
    }

    /**
     * 减少用户数量
     */
    public void decrementUserCount() {
        this.userCount = Math.max(0, (this.userCount == null ? 0 : this.userCount) - 1);
    }

    /**
     * 设置角色层级路径
     */
    public void buildRolePath() {
        if (this.parentId == null) {
            this.rolePath = String.valueOf(this.getId());
        } else {
            this.rolePath = this.parentId + "," + this.getId();
        }
    }

    /**
     * 检查是否为子角色
     */
    public boolean isChildOf(Long parentRoleId) {
        return this.parentId != null && this.parentId.equals(parentRoleId);
    }

    /**
     * 检查是否为根角色
     */
    public boolean isRootRole() {
        return this.parentId == null;
    }

    /**
     * 获取角色完整名称（包含层级）
     */
    public String getFullRoleName() {
        if (this.roleLevel == null || this.roleLevel <= 1) {
            return this.roleName;
        }
        return "  ".repeat(this.roleLevel - 1) + this.roleName;
    }

    /**
     * 检查角色是否需要审批
     */
    public boolean needsApproval() {
        return RoleStatus.PENDING.equals(this.roleStatus);
    }

    /**
     * 获取剩余有效天数
     */
    public long getRemainingDays() {
        if (expiryTime == null) {
            return Long.MAX_VALUE;
        }
        long diff = expiryTime.getTime() - System.currentTimeMillis();
        return Math.max(0, diff / (24 * 60 * 60 * 1000));
    }

    /**
     * 检查是否即将过期（7天内）
     */
    public boolean isExpiringSoon() {
        return getRemainingDays() <= 7 && getRemainingDays() > 0;
    }
}
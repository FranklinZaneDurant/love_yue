package com.loveyue.auth.dto;

import com.loveyue.common.enums.RoleStatus;
import com.loveyue.common.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 角色DTO
 * @Date 2025/8/1
 * @Author LoveYue
 */
@Data
public class RoleEntityDTO {
    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色英文名称")
    private String roleNameEn;

    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "角色类型")
    private RoleType roleType;

    @Schema(description = "角色状态")
    private RoleStatus roleStatus;

    @Schema(description = "父角色ID，支持角色层级结构")
    private Long parentId;

    @Schema(description = "角色层级，数字越小权限越高")
    private Integer roleLevel;

    @Schema(description = "角色路径，用于快速查找父子关系")
    private String rolePath;

    @Schema(description = "排序顺序")
    private Integer sortOrder;

    @Schema(description = "角色权限列表，JSON格式存储")
    private String permissions;

    @Schema(description = "数据权限范围")
    private String dataScope;

    @Schema(description = "部门权限范围，JSON格式存储部门ID列表")
    private String deptIds;

    @Schema(description = "拥有此角色的用户数量")
    private Integer userCount;

    @Schema(description = "是否为默认角色")
    private Boolean defaultRole;

    @Schema(description = "是否为系统内置角色")
    private Boolean system;

    @Schema(description = "角色生效时间")
    private Date effectiveTime;

    @Schema(description = "角色过期时间")
    private Date expiryTime;

    @Schema(description = "审批人ID")
    private Long approvedBy;

    @Schema(description = "审批时间")
    private Date approvedTime;

    @Schema(description = "审批原因")
    private String approvalReason;
}

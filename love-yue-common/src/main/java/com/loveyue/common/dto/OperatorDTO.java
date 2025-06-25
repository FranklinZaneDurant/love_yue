package com.loveyue.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.loveyue.common.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 操作者数据传输对象
 * @Date 2025/6/25
 * @Author LoveYue
 */
@Data
public class OperatorDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -2529510633635460747L;

    @Schema(description = "操作人Id", type = "Long")
    private Long id;

    @Schema(description = "操作人用户名", type = "String")
    private String username;

    @Schema(description = "操作人显示名/昵称", type = "String")
    private String nickname;

    @Schema(description = "邮箱地址", type = "String")
    private String email;

    @Schema(description = "电话号码", type = "String")
    private String phone;

    @Schema(description = "用户状态", type = "UserStatus")
    private UserStatus userStatus;

    @Schema(description = "角色列表", type = "Array")
    private String[] roles;

    @Schema(description = "权限列表", type = "Array")
    private String[] permissions;

    @Schema(description = "所属部门Id", type = "Long")
    private Long departmentId;

    @Schema(description = "所属部门名称", type = "String")
    private String departmentName;

    @Schema(description = "账号创建时间", type = "Date", format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    private Date createTime;

    @Schema(description = "最后更新时间", type = "Date", format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    private Date lastModifyTime;

    @Schema(description = "最后登录时间", type = "Date", format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    private Date lastLoginTime;

    @Schema(description = "密码最后修改时间", type = "Date", format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    private Date lastModifyPasswordTime;

    @Schema(description = "头像URL", type = "String")
    private String avatar;

    @Schema(description = "真实姓名", type = "String")
    private String realName;

    @Schema(description = "职位", type = "String")
    private String jobTitle;

    @Schema(description = "备注", type = "String")
    private String remark;

    @Schema(description = "是否被锁定/禁用", type = "Boolean")
    private Boolean locked;

    @Schema(description = "锁定原因", type = "String")
    private String lockReason;

    @Schema(description = "登录失败次数", type = "Integer")
    private Integer loginFailCount;

    @Schema(description = "客户端IP地址", type = "Integer")
    private String remoteAddr;
}

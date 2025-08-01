package com.loveyue.auth.dto;

import com.loveyue.common.enums.AuthProvider;
import com.loveyue.common.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 用户DTO
 * @Date 2025/7/31
 * @Author LoveYue
 */
@Data
public class UserEntityDTO {
    @Schema(name = "用户名")
    private String username;

    @Schema(name = "密码")
    private String password;

    @Schema(name = "邮箱")
    private String email;

    @Schema(name = "手机号")
    private String phone;

    @Schema(name = "真实姓名")
    private String realName;

    @Schema(name = "昵称")
    private String nickname;

    @Schema(name = "头像URL")
    private String avatarUrl;

    @Schema(name = "用户状态")
    private UserStatus userStatus;

    @Schema(name = "认证提供者")
    private AuthProvider authProvider;

    @Schema(name = "外部认证ID（用于第三方认证）")
    private String externalAuthId;

    @Schema(name = "是否启用多因子认证")
    private Boolean mfaEnabled;

    @Schema(name = "MFA密钥（用于TOTP等）")
    private String mfaSecret;

    @Schema(name = "密码最后修改时间")
    private Date passwordChangedTime;

    @Schema(name = "最后登录时间")
    private Date lastLoginTime;

    @Schema(name = "最后登录IP")
    private String lastLoginIp;

    @Schema(name = "登录失败次数")
    private Integer failedLoginAttempts;

    @Schema(name = "账户锁定时间")
    private Date lockedUntil;

    @Schema(name = "密码过期时间")
    private Date passwordExpiresAt;

    @Schema(name = "账户过期时间")
    private Date accountExpiresAt;

    @Schema(name = "是否强制修改密码")
    private Boolean forcePasswordChange;

    @Schema(name = "用户角色（简化版本，可以是逗号分隔的字符串）")
    private String roles;

    @Schema(name = "用户权限（简化版本，可以是逗号分隔的字符串）")
    private String permissions;

    @Schema(name = "部门ID")
    private Long departmentId;

    @Schema(name = "部门名称")
    private String departmentName;

    @Schema(name = "职位")
    private String position;

    @Schema(name = "备注")
    private String remark;
}

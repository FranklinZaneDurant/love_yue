package com.loveyue.auth.entity;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loveyue.common.entity.BaseBusinessEntity;
import com.loveyue.common.enums.AuthProvider;
import com.loveyue.common.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.util.Date;

/**
 * @Description: 用户实体类
 * @Date 2025/7/31
 * @Author LoveYue
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(indexes = {
        @Index(name = "idx_username", columnList = "username", unique = true),
        @Index(name = "idx_email", columnList = "email", unique = true),
        @Index(name = "idx_phone", columnList = "phone"),
        @Index(name = "idx_user_status", columnList = "user_status"),
        @Index(name = "idx_created_time", columnList = "createdTime")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseBusinessEntity {
    @Serial
    private static final long serialVersionUID = -8561630983317790882L;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    @Schema(name = "用户名")
    private String username;

    @JsonIgnore
    @NotBlank(message = "密码不能为空")
    @Column(name = "password", nullable = false)
    @Schema(name = "密码")
    private String password;

    @Email(message = "邮箱格式不正确")
    @Column(name = "email", nullable = false, length = 100)
    @Schema(name = "邮箱")
    private String email;

    @Size(max = 20, message = "手机号长度不能超过20个字符")
    @Column(name = "phone", length = 20)
    @Schema(name = "手机号")
    private String phone;

    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    @Column(name = "real_name", length = 50)
    @Schema(name = "真实姓名")
    private String realName;

    @Size(max = 50, message = "昵称长度不能超过50个字符")
    @Column(name = "nickname", length = 50)
    @Schema(name = "昵称")
    private String nickname;

    @Column(name = "avatar_url", length = 500)
    @Schema(name = "头像URL")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false, length = 20)
    @Builder.Default
    @Schema(name = "用户状态")
    private UserStatus userStatus = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false, length = 20)
    @Builder.Default
    @Schema(name = "认证提供者")
    private AuthProvider authProvider = AuthProvider.LOCAL;

    @Column(name = "external_auth_id", length = 100)
    @Schema(name = "外部认证ID（用于第三方认证）")
    private String externalAuthId;

    @Column(name = "mfa_enabled", nullable = false)
    @Builder.Default
    @Schema(name = "是否启用多因子认证")
    private Boolean mfaEnabled = false;

    @JsonIgnore
    @Column(name = "mfa_secret", length = 100)
    @Schema(name = "MFA密钥（用于TOTP等）")
    private String mfaSecret;

    @Column(name = "password_changed_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "密码最后修改时间")
    private Date passwordChangedTime;

    @Column(name = "last_login_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "最后登录时间")
    private Date lastLoginTime;

    @Column(name = "last_login_ip", length = 45)
    @Schema(name = "最后登录IP")
    private String lastLoginIp;

    @Column(name = "failed_login_attempts", nullable = false)
    @Builder.Default
    @Schema(name = "登录失败次数")
    private Integer failedLoginAttempts = 0;

    @Column(name = "locked_until")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "账户锁定时间")
    private Date lockedUntil;

    @Column(name = "password_expires_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "密码过期时间")
    private Date passwordExpiresAt;

    @Column(name = "account_expires_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "账户过期时间")
    private Date accountExpiresAt;

    @Column(name = "force_password_change", nullable = false)
    @Builder.Default
    @Schema(name = "是否强制修改密码")
    private Boolean forcePasswordChange = false;

    @Column(name = "roles", length = 500)
    @Schema(name = "用户角色（简化版本，可以是逗号分隔的字符串）")
    private String roles;

    @Column(name = "permissions", length = 1000)
    @Schema(name = "用户权限（简化版本，可以是逗号分隔的字符串）")
    private String permissions;

    @Column(name = "department_id")
    @Schema(name = "部门ID")
    private Long departmentId;

    @Column(name = "department_name", length = 100)
    @Schema(name = "部门名称")
    private String departmentName;

    @Column(name = "position", length = 100)
    @Schema(name = "职位")
    private String position;

    @Column(name = "remark", length = 500)
    @Schema(name = "备注")
    private String remark;

    /**
     * 检查账户是否未过期
     */
    public boolean isAccountNonExpired() {
        return accountExpiresAt == null || DateUtil.date().isBefore(accountExpiresAt);
    }

    /**
     * 检查账户是否未锁定
     */
    public boolean isAccountNonLocked() {
        return !UserStatus.LOCKED.equals(userStatus) &&
               (lockedUntil == null || DateUtil.date().isAfter(lockedUntil));
    }

    /**
     * 检查密码是否未过期
     */
    public boolean isCredentialsNonExpired() {
        return passwordExpiresAt == null || DateUtil.date().isBefore(passwordExpiresAt);
    }

    /**
     * 检查账户是否启用
     */
    public boolean isEnabled() {
        return UserStatus.ACTIVE.equals(userStatus) && !getDeleted();
    }

    /**
     * 检查是否需要强制修改密码
     */
    public boolean needsPasswordChange() {
        return forcePasswordChange || !isCredentialsNonExpired();
    }

    /**
     * 重置登录失败次数
     */
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
    }

    /**
     * 增加登录失败次数
     */
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
    }

    /**
     * 锁定账户
     */
    public void lockAccount(Date lockUntil) {
        this.userStatus = UserStatus.LOCKED;
        this.lockedUntil = lockUntil;
    }

    /**
     * 解锁账户
     */
    public void unlockAccount() {
        if (UserStatus.LOCKED.equals(this.userStatus)) {
            this.userStatus = UserStatus.ACTIVE;
        }
        this.lockedUntil = null;
        this.failedLoginAttempts = 0;
    }

    /**
     * 更新最后登录信息
     */
    public void updateLastLoginInfo(String loginIp) {
        this.lastLoginTime = new Date();
        this.lastLoginIp = loginIp;
        this.resetFailedLoginAttempts();
    }
}

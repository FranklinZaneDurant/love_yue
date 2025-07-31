package com.loveyue.auth.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.loveyue.common.entity.BaseBusinessEntity;
import com.loveyue.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import java.io.Serial;
import java.util.Date;

/**
 * 用户实体类
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_entity", indexes = {
        @Index(name = "idx_username", columnList = "username", unique = true),
        @Index(name = "idx_email", columnList = "email", unique = true),
        @Index(name = "idx_phone", columnList = "phone", unique = true)
})
@Comment("系统用户表")
public class UserEntity extends BaseBusinessEntity {

    @Serial
    private static final long serialVersionUID = 2912297460902850678L;

    @Column(name = "username", nullable = false, length = 50)
    @Comment("用户名")
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    @Comment("密码（加密后）")
    private String password;

    @Column(name = "nickname", length = 100)
    @Comment("昵称")
    private String nickname;

    @Column(name = "real_name", length = 100)
    @Comment("真实姓名")
    private String realName;

    @Column(name = "email", length = 100)
    @Comment("邮箱")
    private String email;

    @Column(name = "phone", length = 20)
    @Comment("手机号")
    private String phone;

    @Column(name = "avatar", length = 500)
    @Comment("头像URL")
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    @Comment("用户状态")
    private UserStatus userStatus = UserStatus.ACTIVE;

    @Column(name = "department_id")
    @Comment("部门ID")
    private Long departmentId;

    @Column(name = "last_login_time")
    @Comment("最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    @Column(name = "last_login_ip", length = 50)
    @Comment("最后登录IP")
    private String lastLoginIp;

    @Column(name = "login_count")
    @Comment("登录次数")
    private Integer loginCount = 0;

    @Column(name = "password_change_time")
    @Comment("密码修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date passwordChangeTime;

    @Column(name = "account_locked")
    @Comment("账户是否锁定")
    private Boolean accountLocked = false;

    @Column(name = "lock_time")
    @Comment("锁定时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lockTime;

    @Column(name = "lock_reason", length = 500)
    @Comment("锁定原因")
    private String lockReason;

    @Column(name = "failed_login_attempts")
    @Comment("失败登录尝试次数")
    private Integer failedLoginAttempts = 0;

    @Column(name = "email_verified")
    @Comment("邮箱是否验证")
    private Boolean emailVerified = false;

    @Column(name = "phone_verified")
    @Comment("手机号是否验证")
    private Boolean phoneVerified = false;

    @Column(name = "two_factor_enabled")
    @Comment("是否启用双因子认证")
    private Boolean twoFactorEnabled = false;

    @Column(name = "two_factor_secret", length = 100)
    @Comment("双因子认证密钥")
    private String twoFactorSecret;

    @Column(name = "remark", length = 500)
    @Comment("备注")
    private String remark;

    /**
     * 增加登录次数
     */
    public void incrementLoginCount() {
        this.loginCount = (this.loginCount == null ? 0 : this.loginCount) + 1;
    }

    /**
     * 重置失败登录尝试次数
     */
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    /**
     * 增加失败登录尝试次数
     */
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts = (this.failedLoginAttempts == null ? 0 : this.failedLoginAttempts) + 1;
    }

    /**
     * 锁定账户
     */
    public void lockAccount() {
        this.accountLocked = true;
        this.lockTime = new Date();
    }

    /**
     * 解锁账户
     */
    public void unlockAccount() {
        this.accountLocked = false;
        this.lockTime = null;
        this.lockReason = null;
        this.failedLoginAttempts = 0;
    }

    /**
     * 更新登录信息
     */
    public void updateLoginInfo(String loginIp) {
        this.lastLoginTime = new Date();
        this.lastLoginIp = loginIp;
        incrementLoginCount();
        resetFailedLoginAttempts();
    }
}
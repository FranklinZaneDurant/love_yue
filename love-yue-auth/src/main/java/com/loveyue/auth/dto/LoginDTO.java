package com.loveyue.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.loveyue.common.enums.UserStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 登录响应DTO
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌类型（通常为Bearer）
     */
    private String tokenType = "Bearer";

    /**
     * 访问令牌过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 刷新令牌过期时间（秒）
     */
    private Long refreshExpiresIn;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

    /**
     * 权限信息
     */
    private PermissionInfo permissions;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 上次登录时间
     */
    private Date lastLoginTime;

    /**
     * 上次登录IP
     */
    private String lastLoginIp;

    /**
     * 密码是否即将过期
     */
    private Boolean passwordExpiringSoon;

    /**
     * 密码过期天数
     */
    private Integer passwordExpiringDays;

    /**
     * 是否需要修改密码
     */
    private Boolean needChangePassword;

    /**
     * 是否首次登录
     */
    private Boolean firstLogin;

    /**
     * 登录设备信息
     */
    private DeviceInfo deviceInfo;

    /**
     * 额外信息
     */
    private Map<String, Object> extra;

    /**
     * 用户信息内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 用户名
         */
        private String username;

        /**
         * 昵称
         */
        private String nickname;

        /**
         * 真实姓名
         */
        private String realName;

        /**
         * 邮箱
         */
        private String email;

        /**
         * 手机号
         */
        private String phone;

        /**
         * 头像URL
         */
        private String avatarUrl;

        /**
         * 部门ID
         */
        private String deptId;

        /**
         * 用户状态
         */
        @Enumerated(EnumType.STRING)
        private UserStatus status;

        /**
         * 用户类型
         */
        private String userType;

        /**
         * 创建时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date createTime;
    }

    /**
     * 权限信息内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PermissionInfo {
        /**
         * 角色列表
         */
        private List<String> roles;

        /**
         * 权限列表
         */
        private List<String> permissions;

        /**
         * 菜单权限
         */
        private List<String> menus;

        /**
         * 按钮权限
         */
        private List<String> buttons;

        /**
         * 数据权限
         */
        private List<String> dataScopes;

        /**
         * 是否超级管理员
         */
        private Boolean isAdmin;
    }

    /**
     * 设备信息内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeviceInfo {
        /**
         * 设备ID
         */
        private String deviceId;

        /**
         * 设备类型
         */
        private String deviceType;

        /**
         * 设备名称
         */
        private String deviceName;

        /**
         * 操作系统
         */
        private String os;

        /**
         * 浏览器
         */
        private String browser;

        /**
         * 客户端IP
         */
        private String clientIp;

        /**
         * 登录位置
         */
        private String location;
    }

    /**
     * 创建成功响应
     */
    public static LoginDTO success(String accessToken, String refreshToken, Long expiresIn, Long refreshExpiresIn) {
        return LoginDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .refreshExpiresIn(refreshExpiresIn)
                .loginTime(new  Date())
                .build();
    }

    /**
     * 设置用户信息
     */
    public LoginDTO withUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    /**
     * 设置权限信息
     */
    public LoginDTO withPermissions(PermissionInfo permissions) {
        this.permissions = permissions;
        return this;
    }

    /**
     * 设置设备信息
     */
    public LoginDTO withDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
        return this;
    }

    /**
     * 设置会话信息
     */
    public LoginDTO withSessionInfo(String sessionId, Date lastLoginTime, String lastLoginIp) {
        this.sessionId = sessionId;
        this.lastLoginTime = lastLoginTime;
        this.lastLoginIp = lastLoginIp;
        return this;
    }

    /**
     * 设置密码状态
     */
    public LoginDTO withPasswordStatus(Boolean needChangePassword, Boolean passwordExpiringSoon, Integer passwordExpiringDays) {
        this.needChangePassword = needChangePassword;
        this.passwordExpiringSoon = passwordExpiringSoon;
        this.passwordExpiringDays = passwordExpiringDays;
        return this;
    }

    /**
     * 设置额外信息
     */
    public LoginDTO withExtra(Map<String, Object> extra) {
        this.extra = extra;
        return this;
    }
}
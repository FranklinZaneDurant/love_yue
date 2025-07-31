package com.loveyue.auth.entity;

import com.loveyue.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 用户登录日志实体类
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_login_log_entity", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_login_time", columnList = "login_time"),
        @Index(name = "idx_login_ip", columnList = "login_ip")
})
@Comment("用户登录日志表")
public class UserLoginLogEntity extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "user_id", nullable = false)
    @Comment("用户ID")
    private Long userId;

    @Column(name = "username", length = 50)
    @Comment("用户名")
    private String username;

    @Column(name = "login_time", nullable = false)
    @Comment("登录时间")
    private LocalDateTime loginTime;

    @Column(name = "logout_time")
    @Comment("登出时间")
    private LocalDateTime logoutTime;

    @Column(name = "login_ip", length = 50)
    @Comment("登录IP")
    private String loginIp;

    @Column(name = "user_agent", length = 500)
    @Comment("用户代理")
    private String userAgent;

    @Column(name = "login_location", length = 100)
    @Comment("登录地点")
    private String loginLocation;

    @Column(name = "login_device", length = 100)
    @Comment("登录设备")
    private String loginDevice;

    @Column(name = "login_browser", length = 100)
    @Comment("登录浏览器")
    private String loginBrowser;

    @Column(name = "login_os", length = 100)
    @Comment("登录操作系统")
    private String loginOs;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_status", nullable = false)
    @Comment("登录状态")
    private LoginStatus loginStatus;

    @Column(name = "login_message", length = 500)
    @Comment("登录消息")
    private String loginMessage;

    @Column(name = "session_id", length = 100)
    @Comment("会话ID")
    private String sessionId;

    @Column(name = "token_id", length = 100)
    @Comment("令牌ID")
    private String tokenId;

    /**
     * 登录状态枚举
     */
    public enum LoginStatus {
        SUCCESS("登录成功"),
        FAILED("登录失败"),
        LOGOUT("登出"),
        TIMEOUT("超时"),
        KICKED_OUT("被踢出");

        private final String description;

        LoginStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 计算在线时长（分钟）
     */
    public Long getOnlineDuration() {
        if (loginTime == null) {
            return 0L;
        }
        LocalDateTime endTime = logoutTime != null ? logoutTime : LocalDateTime.now();
        return java.time.Duration.between(loginTime, endTime).toMinutes();
    }
}
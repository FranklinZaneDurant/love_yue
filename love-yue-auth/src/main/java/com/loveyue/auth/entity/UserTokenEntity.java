package com.loveyue.auth.entity;

import com.loveyue.common.entity.BaseBusinessEntity;
import com.loveyue.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.Comment;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 用户Token实体类
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_token_entity", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_access_token", columnList = "access_token", unique = true),
        @Index(name = "idx_refresh_token", columnList = "refresh_token", unique = true),
        @Index(name = "idx_expire_time", columnList = "expire_time")
})
@Comment("用户Token表")
public class UserTokenEntity extends BaseBusinessEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "user_id", nullable = false)
    @Comment("用户ID")
    private Long userId;

    @Column(name = "access_token", nullable = false, length = 1000)
    @Comment("访问令牌")
    private String accessToken;

    @Column(name = "refresh_token", nullable = false, length = 1000)
    @Comment("刷新令牌")
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    @Comment("令牌类型")
    private TokenType tokenType = TokenType.BEARER;

    @Column(name = "expire_time", nullable = false)
    @Comment("过期时间")
    private LocalDateTime expireTime;

    @Column(name = "refresh_expire_time", nullable = false)
    @Comment("刷新令牌过期时间")
    private LocalDateTime refreshExpireTime;

    @Column(name = "client_ip", length = 50)
    @Comment("客户端IP")
    private String clientIp;

    @Column(name = "user_agent", length = 500)
    @Comment("用户代理")
    private String userAgent;

    @Column(name = "device_id", length = 100)
    @Comment("设备ID")
    private String deviceId;

    @Column(name = "device_type", length = 50)
    @Comment("设备类型")
    private String deviceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_status", nullable = false)
    @Comment("令牌状态")
    private TokenStatus tokenStatus = TokenStatus.ACTIVE;

    @Column(name = "last_used_time")
    @Comment("最后使用时间")
    private LocalDateTime lastUsedTime;

    @Column(name = "revoked_time")
    @Comment("撤销时间")
    private LocalDateTime revokedTime;

    @Column(name = "revoked_by")
    @Comment("撤销人")
    private Long revokedBy;

    @Column(name = "revoke_reason", length = 200)
    @Comment("撤销原因")
    private String revokeReason;

    /**
     * 令牌类型枚举
     */
    @Getter
    public enum TokenType {
        ACCESS("访问令牌"),
        REFRESH("刷新令牌"),
        TEMP("临时令牌"),
        BEARER("Bearer"),
        BASIC("Basic"),
        DIGEST("Digest");

        private final String value;

        TokenType(String value) {
            this.value = value;
        }

    }

    /**
     * 令牌状态枚举
     */
    @Getter
    public enum TokenStatus {
        ACTIVE("活跃"),
        EXPIRED("已过期"),
        REVOKED("已撤销"),
        BLACKLISTED("已拉黑");

        private final String description;

        TokenStatus(String description) {
            this.description = description;
        }

    }

    /**
     * 检查访问令牌是否过期
     */
    public boolean isAccessTokenExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 检查刷新令牌是否过期
     */
    public boolean isRefreshTokenExpired() {
        return refreshExpireTime != null && LocalDateTime.now().isAfter(refreshExpireTime);
    }

    /**
     * 检查令牌是否可用
     */
    public boolean isAvailable() {
        return tokenStatus == TokenStatus.ACTIVE && !isAccessTokenExpired();
    }

    /**
     * 撤销令牌
     */
    public void revoke(Long revokedBy, String reason) {
        this.tokenStatus = TokenStatus.REVOKED;
        this.revokedTime = LocalDateTime.now();
        this.revokedBy = revokedBy;
        this.revokeReason = reason;
    }

    /**
     * 更新最后使用时间
     */
    public void updateLastUsedTime() {
        this.lastUsedTime = LocalDateTime.now();
    }
}
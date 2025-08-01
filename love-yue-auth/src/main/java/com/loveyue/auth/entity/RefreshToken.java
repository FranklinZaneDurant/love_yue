package com.loveyue.auth.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.loveyue.common.entity.BaseBusinessEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.util.Date;

/**
 * @Description: 刷新令牌实体类
 * @Date 2025/8/1
 * @Author LoveYue
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "refresh_token", indexes = {
        @Index(name = "idx_token_value", columnList = "token_value", unique = true),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_expires_at", columnList = "expires_at"),
        @Index(name = "idx_device_id", columnList = "device_id"),
        @Index(name = "idx_is_revoked", columnList = "is_revoked")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken extends BaseBusinessEntity {
    @Serial
    private static final long serialVersionUID = 9092780989605458936L;

    @NotBlank(message = "令牌值不能为空")
    @Column(name = "token_value", nullable = false, unique = true, length = 500)
    @Schema(name = "刷新令牌值")
    private String tokenValue;

    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    @Schema(name = "用户Id")
    private Long userId;

    @NotBlank(message = "用户名不能为空")
    @Column(name = "username", nullable = false, length = 50)
    @Schema(name = "用户名")
    private String username;

    @NotNull(message = "过期时间不能为空")
    @Column(name = "expires_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "过期时间")
    private Date expiresAt;

    @Column(name = "device_id", length = 100)
    @Schema(name = "设备ID")
    private String deviceId;

    @Column(name = "device_type", length = 50)
    @Schema(name = "设备类型")
    private String deviceType;

    @Column(name = "client_ip", length = 45)
    @Schema(name = "客户端IP地址")
    private String clientIp;

    @Column(name = "user_agent", length = 500)
    @Schema(name = "用户代理信息")
    private String userAgent;


    @Column(name = "is_revoked", nullable = false)
    @Schema(name = "是否已撤销")
    private Boolean revoked;

    @Column(name = "revoked_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "撤销时间")
    private Date revokedAt;

    @Column(name = "revoked_by", length = 50)
    @Schema(name = "撤销人")
    private String revokedBy;

    @Column(name = "revoke_reason", length = 200)
    @Schema(name = "撤销原因")
    private String revokeReason;

    @Column(name = "last_used_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "最后使用时间")
    private Date lastUsedAt;

    @Column(name = "use_count", nullable = false)
    @Schema(name = "使用次数")
    private Integer useCount = 0;

    @Column(name = "session_id", length = 100)
    @Schema(name = "会话ID（用于SSO）")
    private String sessionId;

    @Column(name = "scope", length = 200)
    @Schema(name = "令牌作用域")
    private String scope;

    @Column(name = "token_family", length = 100)
    @Schema(name = "令牌族（用于令牌轮换）")
    private String tokenFamily;

    /**
     * 检查令牌是否已过期
     */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.before(new Date());
    }

    /**
     * 检查令牌是否有效（未过期且未撤销）
     */
    public boolean isValid() {
        return !isExpired() && !getRevoked();
    }

    /**
     * 撤销令牌
     */
    public void revoke(String revokedBy, String reason) {
        this.revoked = true;
        this.revokedAt = new Date();
        this.revokedBy = revokedBy;
        this.revokeReason = reason;
    }

    /**
     * 更新最后使用时间和使用次数
     */
    public void updateLastUsed() {
        this.lastUsedAt = new Date();
        this.useCount = this.useCount == null ? 1 : this.useCount + 1;
    }

    /**
     * 检查是否需要轮换（基于使用次数或时间）
     */
    public boolean needsRotation(int maxUseCount, long maxAgeHours) {
        if (this.useCount != null && this.useCount >= maxUseCount) {
            return true;
        }

        if (this.getCreateTime() != null) {
            long ageHours = (System.currentTimeMillis() - this.getCreateTime().getTime()) / (1000 * 60 * 60);
            return ageHours >= maxAgeHours;
        }

        return false;
    }

    /**
     * 获取剩余有效时间（秒）
     */
    public long getRemainingTimeSeconds() {
        if (isExpired() || getRevoked()) {
            return 0;
        }
        return Math.max(0, (expiresAt.getTime() - System.currentTimeMillis()) / 1000);
    }
}

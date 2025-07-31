package com.loveyue.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT工具类
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * JWT密钥
     */
    @Value("${jwt.secret:love-yue-jwt-secret-key-for-authentication-service-2025}")
    private String jwtSecret;

    /**
     * 访问令牌过期时间（分钟）
     */
    @Value("${jwt.access-token-expire-minutes:30}")
    private int accessTokenExpireMinutes;

    /**
     * 刷新令牌过期时间（天）
     */
    @Value("${jwt.refresh-token-expire-days:7}")
    private int refreshTokenExpireDays;

    /**
     * 临时令牌过期时间（分钟）
     */
    @Value("${jwt.temp-token-expire-minutes:5}")
    private int tempTokenExpireMinutes;

    /**
     * JWT发行者
     */
    @Value("${jwt.issuer:love-yue-auth}")
    private String issuer;

    /**
     * JWT受众
     */
    @Value("${jwt.audience:love-yue-client}")
    private String audience;

    // JWT声明常量
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_DEPT_ID = "deptId";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_PERMISSIONS = "permissions";
    public static final String CLAIM_TOKEN_TYPE = "tokenType";
    public static final String CLAIM_DEVICE_ID = "deviceId";
    public static final String CLAIM_CLIENT_IP = "clientIp";
    public static final String CLAIM_SESSION_ID = "sessionId";

    // Token类型常量
    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";
    public static final String TOKEN_TYPE_TEMP = "temp";

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(
            Long userId,
            String username,
            List<String> roles,
            List<String> permissions,
            String deviceId,
            String clientIp,
            String sessionId
    ) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_USERNAME, username);
        claims.put(CLAIM_ROLES, roles);
        claims.put(CLAIM_PERMISSIONS, permissions);
        claims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS);
        claims.put(CLAIM_DEVICE_ID, deviceId);
        claims.put(CLAIM_CLIENT_IP, clientIp);
        claims.put(CLAIM_SESSION_ID, sessionId);

        return generateToken(claims, accessTokenExpireMinutes * 60);
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId, String username, String deviceId, String sessionId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_USERNAME, username);
        claims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH);
        claims.put(CLAIM_DEVICE_ID, deviceId);
        claims.put(CLAIM_SESSION_ID, sessionId);

        return generateToken(claims, refreshTokenExpireDays * 24 * 60 * 60);
    }

    /**
     * 生成临时令牌
     */
    public String generateTempToken(Long userId, String username, String purpose, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_USERNAME, username);
        claims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_TEMP);
        claims.put("purpose", purpose);
        
        if (extraClaims != null) {
            claims.putAll(extraClaims);
        }

        return generateToken(claims, tempTokenExpireMinutes * 60);
    }

    /**
     * 生成JWT令牌
     */
    private String generateToken(Map<String, Object> claims, int expireSeconds) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + expireSeconds * 1000L);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(now)
                .setExpiration(expireTime)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析JWT令牌
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token已过期: {}", e.getMessage());
            throw new RuntimeException("Token已过期", e);
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT token: {}", e.getMessage());
            throw new RuntimeException("不支持的Token格式", e);
        } catch (MalformedJwtException e) {
            log.error("JWT token格式错误: {}", e.getMessage());
            throw new RuntimeException("Token格式错误", e);
        } catch (SecurityException e) {
            log.error("JWT token签名验证失败: {}", e.getMessage());
            throw new RuntimeException("Token签名验证失败", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT token参数错误: {}", e.getMessage());
            throw new RuntimeException("Token参数错误", e);
        }
    }

    /**
     * 验证JWT令牌
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return isTokenExpired(claims);
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 检查令牌是否过期
     */
    public boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        Object userId = claims.get(CLAIM_USER_ID);
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    /**
     * 从令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get(CLAIM_USERNAME);
    }

    /**
     * 从令牌中获取部门ID
     */
    public String getDeptIdFromToken(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get(CLAIM_DEPT_ID);
    }

    /**
     * 从令牌中获取角色列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = parseToken(token);
        return (List<String>) claims.get(CLAIM_ROLES);
    }

    /**
     * 从令牌中获取权限列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getPermissionsFromToken(String token) {
        Claims claims = parseToken(token);
        return (List<String>) claims.get(CLAIM_PERMISSIONS);
    }

    /**
     * 从令牌中获取令牌类型
     */
    public String getTokenTypeFromToken(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get(CLAIM_TOKEN_TYPE);
    }

    /**
     * 从令牌中获取设备ID
     */
    public String getDeviceIdFromToken(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get(CLAIM_DEVICE_ID);
    }

    /**
     * 从令牌中获取客户端IP
     */
    public String getClientIpFromToken(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get(CLAIM_CLIENT_IP);
    }

    /**
     * 从令牌中获取会话ID
     */
    public String getSessionIdFromToken(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get(CLAIM_SESSION_ID);
    }

    /**
     * 获取令牌过期时间
     */
    public LocalDateTime getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        Date expiration = claims.getExpiration();
        return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 获取令牌签发时间
     */
    public LocalDateTime getIssuedAtFromToken(String token) {
        Claims claims = parseToken(token);
        Date issuedAt = claims.getIssuedAt();
        return issuedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 检查令牌是否为指定类型
     */
    public boolean isTokenType(String token, String tokenType) {
        try {
            String actualType = getTokenTypeFromToken(token);
            return StrUtil.equals(actualType, tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查是否为访问令牌
     */
    public boolean isAccessToken(String token) {
        return isTokenType(token, TOKEN_TYPE_ACCESS);
    }

    /**
     * 检查是否为刷新令牌
     */
    public boolean isRefreshToken(String token) {
        return !isTokenType(token, TOKEN_TYPE_REFRESH);
    }

    /**
     * 检查是否为临时令牌
     */
    public boolean isTempToken(String token) {
        return isTokenType(token, TOKEN_TYPE_TEMP);
    }

    /**
     * 获取令牌剩余有效时间（秒）
     */
    public long getTokenRemainingTime(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            long remainingTime = (expiration.getTime() - System.currentTimeMillis()) / 1000;
            return Math.max(0, remainingTime);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 刷新访问令牌（基于刷新令牌）
     */
    public String refreshAccessToken(String refreshToken, List<String> roles, List<String> permissions, String clientIp) {
        if (isRefreshToken(refreshToken) || validateToken(refreshToken)) {
            throw new RuntimeException("无效的刷新令牌");
        }

        Long userId = getUserIdFromToken(refreshToken);
        String username = getUsernameFromToken(refreshToken);
        String deviceId = getDeviceIdFromToken(refreshToken);
        String sessionId = getSessionIdFromToken(refreshToken);

        // 从刷新令牌中获取部门信息（如果有）
        Claims claims = parseToken(refreshToken);
        String deptId = (String) claims.get(CLAIM_DEPT_ID);

        return generateAccessToken(userId, username, roles, permissions, deviceId, clientIp, sessionId);
    }

    /**
     * 获取令牌的所有声明信息
     */
    public Map<String, Object> getAllClaimsFromToken(String token) {
        Claims claims = parseToken(token);
        return new HashMap<>(claims);
    }

    /**
     * 打印令牌信息（用于调试）
     */
    public void printTokenInfo(String token) {
        try {
            Claims claims = parseToken(token);
            log.info("Token信息: {}", JSONUtil.toJsonPrettyStr(claims));
        } catch (Exception e) {
            log.error("解析Token失败: {}", e.getMessage());
        }
    }
}
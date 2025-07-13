package com.loveyue.gateway.util;

import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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
     * Token即将过期的阈值时间（5分钟）
     */
    private static final long TOKEN_EXPIRING_THRESHOLD = 5 * 60 * 1000L;
    
    /**
     * 最小密钥长度（256位 = 32字节）
     */
    private static final int MIN_SECRET_LENGTH = 32;
    
    /**
     * Claims缓存，避免重复解析同一token
     */
    private final Map<String, Claims> claimsCache = new ConcurrentHashMap<>();

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;
    
    /**
     * 初始化配置验证
     */
    @PostConstruct
    public void init() {
        validateConfiguration();
        log.info("JWT实用程序初始化成功，过期时间：{}ms", expiration);
    }
    
    /**
     * 验证配置的有效性
     */
    private void validateConfiguration() {
        if (StrUtil.isBlank(secret)) {
            throw new IllegalArgumentException("JWT secret不能为空");
        }
        
        if (secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_LENGTH) {
            throw new IllegalArgumentException(
                String.format("对于HMAC SHA256， JWT密钥长度必须至少为%d字节", MIN_SECRET_LENGTH)
            );
        }
        
        if (expiration == null || expiration <= 0) {
            throw new IllegalArgumentException("JWT过期必须是一个正数");
        }
        
        log.info("JWT配置验证通过");
    }

    /**
     * 生成JWT token
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param claims 额外声明
     * @return JWT token
     */
    public String generateToken(String userId, String username, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        JwtBuilder builder = Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSignKey());
        
        if (claims != null && !claims.isEmpty()) {
            builder.claims(claims);
        }
        
        return builder.compact();
    }

    /**
     * 从token中获取用户ID
     *
     * @param token JWT token
     * @return 用户ID的Optional包装
     */
    public Optional<String> getUserIdFromToken(String token) {
        return getClaimsFromToken(token)
                .map(Claims::getSubject);
    }

    /**
     * 从token中获取用户名
     *
     * @param token JWT token
     * @return 用户名的Optional包装
     */
    public Optional<String> getUsernameFromToken(String token) {
        return getClaimsFromToken(token)
                .map(claims -> (String) claims.get("username"));
    }

    /**
     * 从token中获取过期时间
     *
     * @param token JWT token
     * @return 过期时间的Optional包装
     */
    public Optional<Date> getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token)
                .map(Claims::getExpiration);
    }

    /**
     * 验证token是否有效
     *
     * @param token JWT token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            if (StrUtil.isBlank(token)) {
                log.debug("令牌验证失败：令牌为空");
                return false;
            }
            
            return getClaimsFromToken(token)
                    .map(claims -> {
                        Date expiration = claims.getExpiration();
                        boolean isValid = expiration != null && !expiration.before(new Date());
                        if (!isValid) {
                            log.debug("令牌验证失败：令牌在{}处过期", expiration);
                        }
                        return isValid;
                    })
                    .orElse(false);
        } catch (Exception e) {
            log.error("令牌验证失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查token是否即将过期（剩余时间少于5分钟）
     *
     * @param token JWT token
     * @return 是否即将过期
     */
    public boolean isTokenExpiringSoon(String token) {
        try {
            return getExpirationDateFromToken(token)
                    .map(expiration -> {
                        long timeLeft = expiration.getTime() - System.currentTimeMillis();
                        return timeLeft < TOKEN_EXPIRING_THRESHOLD;
                    })
                    .orElse(true);
        } catch (Exception e) {
            log.warn("检查令牌过期时出错：{}", e.getMessage());
            return true;
        }
    }
    
    /**
     * 获取token剩余有效时间（毫秒）
     *
     * @param token JWT token
     * @return 剩余时间的Optional包装，如果token无效则返回empty
     */
    public Optional<Long> getTokenRemainingTime(String token) {
        return getExpirationDateFromToken(token)
                .map(expiration -> Math.max(0, expiration.getTime() - System.currentTimeMillis()));
    }
    
    /**
     * 清除指定token的缓存
     *
     * @param token JWT token
     */
    public void clearTokenCache(String token) {
        if (StrUtil.isNotBlank(token)) {
            claimsCache.remove(token);
        }
    }
    
    /**
     * 清除所有token缓存
     */
    public void clearAllTokenCache() {
        claimsCache.clear();
        log.debug("清除所有令牌缓存");
    }

    /**
     * 从token中解析Claims（带缓存）
     *
     * @param token JWT token
     * @return Claims的Optional包装
     */
    private Optional<Claims> getClaimsFromToken(String token) {
        if (StrUtil.isBlank(token)) {
            return Optional.empty();
        }
        
        // 先从缓存中获取
        Claims cachedClaims = claimsCache.get(token);
        if (cachedClaims != null) {
            // 检查缓存的claims是否还有效
            if (cachedClaims.getExpiration() != null && 
                !cachedClaims.getExpiration().before(new Date())) {
                return Optional.of(cachedClaims);
            } else {
                // 缓存的token已过期，移除缓存
                claimsCache.remove(token);
            }
        }
        
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            // 将有效的claims放入缓存
            if (claims.getExpiration() != null && 
                !claims.getExpiration().before(new Date())) {
                claimsCache.put(token, claims);
            }
            
            return Optional.of(claims);
        } catch (ExpiredJwtException e) {
            log.debug("令牌过期：{}", e.getMessage());
            return Optional.empty();
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的JWT令牌：{}", e.getMessage());
            return Optional.empty();
        } catch (MalformedJwtException e) {
            log.warn("无效JWT令牌格式：{}", e.getMessage());
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            log.warn("JWT令牌参数无效：{}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("解析JWT令牌时出现意外错误：{}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 获取签名密钥
     *
     * @return 签名密钥
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
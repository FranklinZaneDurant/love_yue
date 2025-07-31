package com.loveyue.auth.service.impl;

import com.loveyue.auth.dto.TokenValidationDTO;
import com.loveyue.auth.service.TokenValidationService;
import com.loveyue.common.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 令牌验证服务实现
 * 
 * @author loveyue
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenValidationServiceImpl implements TokenValidationService {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_BLACKLIST_PREFIX = "auth:token:blacklist:";

    @Override
    public TokenValidationDTO validateToken(String token) {
        try {
            // 检查令牌是否在黑名单中
            if (isTokenBlacklisted(token)) {
                return TokenValidationDTO.builder()
                        .valid(false)
                        .errorMessage("令牌已被加入黑名单")
                        .build();
            }

            // 验证令牌格式和签名
            if (jwtUtil.validateToken(token)) {
                return TokenValidationDTO.builder()
                        .valid(false)
                        .errorMessage("令牌无效或已过期")
                        .build();
            }

            // 提取令牌信息
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            String deptId = jwtUtil.getDeptIdFromToken(token);
            List<String> roles = jwtUtil.getRolesFromToken(token);
            List<String> permissions = jwtUtil.getPermissionsFromToken(token);
            String tokenType = jwtUtil.getTokenTypeFromToken(token);
            String deviceId = jwtUtil.getDeviceIdFromToken(token);
            String clientIp = jwtUtil.getClientIpFromToken(token);
            String sessionId = jwtUtil.getSessionIdFromToken(token);
            
            return TokenValidationDTO.builder()
                    .valid(true)
                    .userId(userId)
                    .username(username)
                    .deptId(deptId)
                    .roles(roles)
                    .permissions(permissions)
                    .tokenType(tokenType)
                    .deviceId(deviceId)
                    .clientIp(clientIp)
                    .sessionId(sessionId)
                    .expiration(jwtUtil.getExpirationFromToken(token))
                    .remainingTime(jwtUtil.getTokenRemainingTime(token))
                    .build();

        } catch (Exception e) {
            log.error("令牌验证失败: {}", e.getMessage());
            return TokenValidationDTO.builder()
                    .valid(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        try {
            String key = TOKEN_BLACKLIST_PREFIX + token;
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("检查令牌黑名单失败: {}", e.getMessage());
            return false;
        }
    }
}
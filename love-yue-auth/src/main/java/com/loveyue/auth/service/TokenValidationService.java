package com.loveyue.auth.service;

import com.loveyue.auth.dto.TokenValidationDTO;

/**
 * 令牌验证服务接口
 * 
 * @author loveyue
 * @since 2025-01-20
 */
public interface TokenValidationService {

    /**
     * 验证令牌
     * 
     * @param token JWT令牌
     * @return 验证结果
     */
    TokenValidationDTO validateToken(String token);

    /**
     * 检查令牌是否在黑名单中
     * 
     * @param token JWT令牌
     * @return 是否在黑名单中
     */
    boolean isTokenBlacklisted(String token);
}
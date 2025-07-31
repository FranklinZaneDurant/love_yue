package com.loveyue.gateway.dto;

import lombok.Data;

/**
 * 令牌验证请求
 * 
 * @author loveyue
 * @since 2025-01-20
 */
@Data
public class TokenValidationRequest {

    /**
     * JWT令牌
     */
    private String token;
}
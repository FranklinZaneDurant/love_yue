package com.loveyue.gateway.dto;

import lombok.Data;

/**
 * 令牌验证API响应
 *
 * @author loveyue
 * @date 2024-01-01
 */
@Data
public class TokenValidationApiResponse {
    
    /**
     * 响应状态码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private TokenValidationResponse data;
    
    /**
     * 是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return code != null && code == 200;
    }
}
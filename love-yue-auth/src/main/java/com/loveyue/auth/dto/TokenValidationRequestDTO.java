package com.loveyue.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 令牌验证请求
 * 
 * @author loveyue
 * @since 2025-01-20
 */
@Data
@Schema(description = "令牌验证请求")
public class TokenValidationRequestDTO {

    /**
     * JWT令牌
     */
    @Schema(description = "JWT令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @NotBlank(message = "令牌不能为空")
    private String token;
}
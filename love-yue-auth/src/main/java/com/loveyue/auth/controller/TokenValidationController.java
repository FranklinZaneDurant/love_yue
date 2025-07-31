package com.loveyue.auth.controller;

import com.loveyue.auth.dto.TokenValidationRequestDTO;
import com.loveyue.auth.dto.TokenValidationDTO;
import com.loveyue.auth.service.TokenValidationService;
import com.loveyue.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 令牌验证控制器
 * 提供给Gateway服务调用的令牌验证接口
 * 
 * @author loveyue
 * @since 2025-01-20
 */
@Tag(name = "令牌验证", description = "令牌验证相关接口")
@RestController
@RequestMapping("/api/auth/token")
@RequiredArgsConstructor
public class TokenValidationController {

    private final TokenValidationService tokenValidationService;

    /**
     * 验证令牌
     */
    @Operation(summary = "验证令牌", description = "验证JWT令牌的有效性并返回用户信息")
    @PostMapping("/validate")
    public BaseResponse<TokenValidationDTO> validateToken(@Valid @RequestBody TokenValidationRequestDTO request) {
        TokenValidationDTO response = tokenValidationService.validateToken(request.getToken());
        return BaseResponse.success(response);
    }

    /**
     * 检查令牌是否存在于黑名单
     */
    @Operation(summary = "检查令牌黑名单", description = "检查令牌是否在黑名单中")
    @GetMapping("/blacklist/check")
    public BaseResponse<Boolean> checkTokenBlacklist(@RequestParam String token) {
        boolean isBlacklisted = tokenValidationService.isTokenBlacklisted(token);
        return BaseResponse.success(isBlacklisted);
    }
}
package com.loveyue.auth.controller;

import com.loveyue.auth.dto.LoginRequestDTO;
import com.loveyue.auth.dto.LoginDTO;
import com.loveyue.auth.dto.RefreshTokenDTO;
import com.loveyue.auth.dto.LogoutRequestDTO;
import com.loveyue.auth.entity.UserEntity;
import com.loveyue.auth.entity.UserTokenEntity;
import com.loveyue.auth.service.AuthService;
import com.loveyue.common.controller.BaseController;
import com.loveyue.common.response.ObjectResponse;
import com.loveyue.common.response.ListObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * 认证控制器
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController extends BaseController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录认证")
    public ObjectResponse<LoginDTO> login(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO,
            HttpServletRequest request) {
        
        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        
        try {
            LoginDTO response = authService.login(loginRequestDTO, clientIp, userAgent);
            return success(response);
        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出")
    public ObjectResponse<Boolean> logout(
            @Valid @RequestBody LogoutRequestDTO logoutRequestDTO,
            HttpServletRequest request) {
        
        String clientIp = getClientIp(request);
        
        try {
            boolean success = authService.logout(logoutRequestDTO, clientIp);
            return success(success);
        } catch (Exception e) {
            log.error("用户登出失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public ObjectResponse<LoginDTO> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO,
            HttpServletRequest request) {
        
        String clientIp = getClientIp(request);
        
        try {
            LoginDTO response = authService.refreshToken(refreshTokenDTO, clientIp);
            return success(response);
        } catch (Exception e) {
            log.error("刷新令牌失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "验证令牌", description = "验证访问令牌是否有效")
    public ObjectResponse<Boolean> validateToken(
            @Parameter(description = "访问令牌") @RequestParam @NotBlank String accessToken) {
        
        try {
            boolean valid = authService.validateAccessToken(accessToken);
            return success(valid);
        } catch (Exception e) {
            log.error("验证令牌失败: {}", e.getMessage());
            return success(false);
        }
    }

    @GetMapping("/user-info")
    @Operation(summary = "获取用户信息", description = "从令牌中获取用户信息")
    public ObjectResponse<UserEntity> getUserInfo(
            @Parameter(description = "访问令牌") @RequestParam @NotBlank String accessToken) {
        
        try {
            UserEntity userEntity = authService.getUserFromToken(accessToken);
            if (userEntity != null) {
                // 清除敏感信息
                userEntity.setPassword(null);
                return success(userEntity);
            } else {
                return fail("无效的令牌");
            }
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @GetMapping("/user-roles")
    @Operation(summary = "获取用户角色", description = "从令牌中获取用户角色")
    public ObjectResponse<List<String>> getUserRoles(
            @Parameter(description = "访问令牌") @RequestParam @NotBlank String accessToken) {
        
        try {
            List<String> roles = authService.getUserRolesFromToken(accessToken);
            return success(roles);
        } catch (Exception e) {
            log.error("获取用户角色失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @GetMapping("/user-permissions")
    @Operation(summary = "获取用户权限", description = "从令牌中获取用户权限")
    public ObjectResponse<List<String>> getUserPermissions(
            @Parameter(description = "访问令牌") @RequestParam @NotBlank String accessToken) {
        
        try {
            List<String> permissions = authService.getUserPermissionsFromToken(accessToken);
            return success(permissions);
        } catch (Exception e) {
            log.error("获取用户权限失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @PostMapping("/revoke")
    @Operation(summary = "撤销令牌", description = "撤销指定的访问令牌")
    public ObjectResponse<Boolean> revokeToken(
            @Parameter(description = "访问令牌") @RequestParam @NotBlank String accessToken,
            @Parameter(description = "撤销原因") @RequestParam(required = false) String reason) {
        
        try {
            Long currentUserId = getCurrentUserId();
            boolean success = authService.revokeToken(accessToken, currentUserId, reason);
            return success(success);
        } catch (Exception e) {
            log.error("撤销令牌失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @PostMapping("/revoke-all/{userId}")
    @Operation(summary = "撤销用户所有令牌", description = "撤销指定用户的所有令牌")
    public ObjectResponse<Integer> revokeAllUserTokens(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "撤销原因") @RequestParam(required = false) String reason) {
        
        try {
            Long currentUserId = getCurrentUserId();
            int revokedCount = authService.revokeAllUserTokens(userId, currentUserId, reason);
            return success(revokedCount);
        } catch (Exception e) {
            log.error("撤销用户所有令牌失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @PostMapping("/force-offline/{userId}")
    @Operation(summary = "强制用户下线", description = "强制指定用户下线")
    public ObjectResponse<Integer> forceUserOffline(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "下线原因") @RequestParam(required = false) String reason) {
        
        try {
            Long currentUserId = getCurrentUserId();
            int offlineCount = authService.forceUserOffline(userId, currentUserId, reason);
            return success(offlineCount);
        } catch (Exception e) {
            log.error("强制用户下线失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @GetMapping("/online-status/{userId}")
    @Operation(summary = "检查用户在线状态", description = "检查指定用户是否在线")
    public ObjectResponse<Boolean> checkUserOnlineStatus(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        
        try {
            boolean online = authService.isUserOnline(userId);
            return success(online);
        } catch (Exception e) {
            log.error("检查用户在线状态失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @GetMapping("/active-tokens/{userId}")
    @Operation(summary = "获取用户活跃令牌", description = "获取指定用户的所有活跃令牌")
    public ListObjectResponse<UserTokenEntity> getUserActiveTokens(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        
        try {
            List<UserTokenEntity> tokens = authService.getUserActiveTokens(userId);
            // 清除敏感信息
            tokens.forEach(token -> {
                token.setAccessToken("***");
                token.setRefreshToken("***");
            });
            return success(tokens);
        } catch (Exception e) {
            log.error("获取用户活跃令牌失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @GetMapping("/recent-logins/{userId}")
    @Operation(summary = "获取用户最近登录记录", description = "获取指定用户的最近登录记录")
    public ListObjectResponse<Map<String, Object>> getUserRecentLogins(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "记录数量") @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<Map<String, Object>> logins = authService.getUserRecentLogins(userId, limit);
            return success(logins);
        } catch (Exception e) {
            log.error("获取用户最近登录记录失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @PostMapping("/temp-token")
    @Operation(summary = "生成临时令牌", description = "生成临时访问令牌")
    public ObjectResponse<String> generateTempToken(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "用途") @RequestParam @NotBlank String purpose,
            @Parameter(description = "过期时间（分钟）") @RequestParam(required = false) Integer expireMinutes) {
        
        try {
            String tempToken = authService.generateTempToken(userId, purpose, null, expireMinutes);
            return success(tempToken);
        } catch (Exception e) {
            log.error("生成临时令牌失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @PostMapping("/validate-temp-token")
    @Operation(summary = "验证临时令牌", description = "验证临时令牌是否有效")
    public ObjectResponse<Boolean> validateTempToken(
            @Parameter(description = "临时令牌") @RequestParam @NotBlank String tempToken,
            @Parameter(description = "预期用途") @RequestParam @NotBlank String purpose) {
        
        try {
            boolean valid = authService.validateTempToken(tempToken, purpose);
            return success(valid);
        } catch (Exception e) {
            log.error("验证临时令牌失败: {}", e.getMessage());
            return success(false);
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取令牌统计信息", description = "获取系统令牌统计信息")
    public ObjectResponse<Map<String, Object>> getTokenStatistics() {
        try {
            Map<String, Object> statistics = authService.getTokenStatistics();
            return success(statistics);
        } catch (Exception e) {
            log.error("获取令牌统计信息失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @PostMapping("/cleanup-expired")
    @Operation(summary = "清理过期令牌", description = "清理系统中的过期令牌")
    public ObjectResponse<Integer> cleanupExpiredTokens() {
        try {
            int cleanedCount = authService.cleanupExpiredTokens();
            return success(cleanedCount);
        } catch (Exception e) {
            log.error("清理过期令牌失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @PostMapping("/lock-account/{userId}")
    @Operation(summary = "锁定用户账户", description = "锁定指定用户账户")
    public ObjectResponse<Boolean> lockUserAccount(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "锁定原因") @RequestParam(required = false) String reason) {
        
        try {
            Long currentUserId = getCurrentUserId();
            authService.lockUserAccount(userId, reason, currentUserId);
            return success(true);
        } catch (Exception e) {
            log.error("锁定用户账户失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    @PostMapping("/unlock-account/{userId}")
    @Operation(summary = "解锁用户账户", description = "解锁指定用户账户")
    public ObjectResponse<Boolean> unlockUserAccount(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        
        try {
            Long currentUserId = getCurrentUserId();
            authService.unlockUserAccount(userId, currentUserId);
            return success(true);
        } catch (Exception e) {
            log.error("解锁用户账户失败: {}", e.getMessage());
            return fail(e.getMessage());
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        String xRealIp = request.getHeader("X-Real-IP");
        String remoteAddr = request.getRemoteAddr();
        
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        return remoteAddr;
    }
}
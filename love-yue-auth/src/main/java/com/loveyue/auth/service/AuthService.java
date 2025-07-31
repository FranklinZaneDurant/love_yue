package com.loveyue.auth.service;

import com.loveyue.auth.dto.LoginRequestDTO;
import com.loveyue.auth.dto.LoginDTO;
import com.loveyue.auth.dto.RefreshTokenDTO;
import com.loveyue.auth.dto.LogoutRequestDTO;
import com.loveyue.auth.entity.UserEntity;
import com.loveyue.auth.entity.UserTokenEntity;

import java.util.List;
import java.util.Map;

/**
 * 认证服务接口
 * 
 * @author loveyue
 * @since 2025-07-13
 */
public interface AuthService {

    /**
     * 用户登录
     * 
     * @param loginRequestDTO 登录请求
     * @param clientIp 客户端IP
     * @param userAgent 用户代理
     * @return 登录响应
     */
    LoginDTO login(LoginRequestDTO loginRequestDTO, String clientIp, String userAgent);

    /**
     * 用户登出
     * 
     * @param logoutRequestDTO 登出请求
     * @param clientIp 客户端IP
     * @return 是否成功
     */
    boolean logout(LogoutRequestDTO logoutRequestDTO, String clientIp);

    /**
     * 刷新访问令牌
     * 
     * @param refreshTokenDTO 刷新令牌请求
     * @param clientIp 客户端IP
     * @return 新的访问令牌信息
     */
    LoginDTO refreshToken(RefreshTokenDTO refreshTokenDTO, String clientIp);

    /**
     * 验证访问令牌
     * 
     * @param accessToken 访问令牌
     * @return 是否有效
     */
    boolean validateAccessToken(String accessToken);

    /**
     * 验证刷新令牌
     * 
     * @param refreshToken 刷新令牌
     * @return 是否有效
     */
    boolean validateRefreshToken(String refreshToken);

    /**
     * 撤销用户所有令牌
     * 
     * @param userId 用户ID
     * @param revokedBy 撤销人
     * @param reason 撤销原因
     * @return 撤销的令牌数量
     */
    int revokeAllUserTokens(Long userId, Long revokedBy, String reason);

    /**
     * 撤销指定令牌
     * 
     * @param accessToken 访问令牌
     * @param revokedBy 撤销人
     * @param reason 撤销原因
     * @return 是否成功
     */
    boolean revokeToken(String accessToken, Long revokedBy, String reason);

    /**
     * 撤销用户指定设备的令牌
     * 
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param revokedBy 撤销人
     * @param reason 撤销原因
     * @return 撤销的令牌数量
     */
    int revokeUserDeviceTokens(Long userId, String deviceId, Long revokedBy, String reason);

    /**
     * 获取用户信息（从令牌）
     * 
     * @param accessToken 访问令牌
     * @return 用户信息
     */
    UserEntity getUserFromToken(String accessToken);

    /**
     * 获取用户角色（从令牌）
     * 
     * @param accessToken 访问令牌
     * @return 角色列表
     */
    List<String> getUserRolesFromToken(String accessToken);

    /**
     * 获取用户权限（从令牌）
     * 
     * @param accessToken 访问令牌
     * @return 权限列表
     */
    List<String> getUserPermissionsFromToken(String accessToken);

    /**
     * 生成临时访问令牌
     * 
     * @param userId 用户ID
     * @param purpose 用途
     * @param extraClaims 额外声明
     * @param expireMinutes 过期时间（分钟）
     * @return 临时令牌
     */
    String generateTempToken(Long userId, String purpose, Map<String, Object> extraClaims, Integer expireMinutes);

    /**
     * 验证临时令牌
     * 
     * @param tempToken 临时令牌
     * @param purpose 预期用途
     * @return 是否有效
     */
    boolean validateTempToken(String tempToken, String purpose);

    /**
     * 检查用户是否在线
     * 
     * @param userId 用户ID
     * @return 是否在线
     */
    boolean isUserOnline(Long userId);

    /**
     * 获取用户活跃令牌数量
     * 
     * @param userId 用户ID
     * @return 活跃令牌数量
     */
    long getUserActiveTokenCount(Long userId);

    /**
     * 获取用户所有活跃令牌
     * 
     * @param userId 用户ID
     * @return 活跃令牌列表
     */
    List<UserTokenEntity> getUserActiveTokens(Long userId);

    /**
     * 强制用户下线
     * 
     * @param userId 用户ID
     * @param operatorId 操作人ID
     * @param reason 下线原因
     * @return 下线的会话数量
     */
    int forceUserOffline(Long userId, Long operatorId, String reason);

    /**
     * 单点登录检查
     *
     * @param userId               用户ID
     * @param deviceId             设备ID
     * @param allowMultipleDevices 是否允许多设备登录
     */
    void singleSignOnCheck(Long userId, String deviceId, boolean allowMultipleDevices);

    /**
     * 更新令牌最后使用时间
     * 
     * @param accessToken 访问令牌
     */
    void updateTokenLastUsedTime(String accessToken);

    /**
     * 清理过期令牌
     * 
     * @return 清理的令牌数量
     */
    int cleanupExpiredTokens();

    /**
     * 获取令牌统计信息
     * 
     * @return 统计信息
     */
    Map<String, Object> getTokenStatistics();

    /**
     * 验证用户密码
     * 
     * @param username 用户名
     * @param password 密码
     * @return 用户信息（验证成功时）
     */
    UserEntity validateUserCredentials(String username, String password);

    /**
     * 记录登录失败
     * 
     * @param username 用户名
     * @param clientIp 客户端IP
     * @param userAgent 用户代理
     * @param reason 失败原因
     */
    void recordLoginFailure(String username, String clientIp, String userAgent, String reason);

    /**
     * 记录登录成功
     * 
     * @param userEntity 用户信息
     * @param clientIp 客户端IP
     * @param userAgent 用户代理
     * @param sessionId 会话ID
     * @param tokenId 令牌ID
     */
    void recordLoginSuccess(UserEntity userEntity, String clientIp, String userAgent, String sessionId, String tokenId);

    /**
     * 检查账户锁定状态
     * 
     * @param username 用户名
     * @return 是否被锁定
     */
    boolean isAccountLocked(String username);

    /**
     * 锁定用户账户
     * 
     * @param userId 用户ID
     * @param reason 锁定原因
     * @param operatorId 操作人ID
     */
    void lockUserAccount(Long userId, String reason, Long operatorId);

    /**
     * 解锁用户账户
     * 
     * @param userId 用户ID
     * @param operatorId 操作人ID
     */
    void unlockUserAccount(Long userId, Long operatorId);

    /**
     * 获取用户最近登录记录
     * 
     * @param userId 用户ID
     * @param limit 记录数量限制
     * @return 登录记录列表
     */
    List<Map<String, Object>> getUserRecentLogins(Long userId, int limit);

    /**
     * 检查可疑登录
     * 
     * @param userId 用户ID
     * @param clientIp 客户端IP
     * @param userAgent 用户代理
     * @return 是否可疑
     */
    boolean isSuspiciousLogin(Long userId, String clientIp, String userAgent);
}
package com.loveyue.auth.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.loveyue.auth.dto.LoginRequestDTO;
import com.loveyue.auth.dto.LoginDTO;
import com.loveyue.auth.dto.RefreshTokenDTO;
import com.loveyue.auth.dto.LogoutRequestDTO;
import com.loveyue.auth.entity.*;
import com.loveyue.auth.repository.*;
import com.loveyue.auth.service.AuthService;
import com.loveyue.auth.service.TokenUpdateService;
import com.loveyue.auth.util.PasswordUtil;
import com.loveyue.common.utils.JwtUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Resource
    private final UserRepository userRepository;

    @Resource
    private final RoleRepository roleRepository;

    @Resource
    private final UserRoleRepository userRoleRepository;

    @Resource
    private final UserTokenRepository userTokenRepository;

    @Resource
    private final UserLoginLogRepository userLoginLogRepository;

    @Resource
    private final JwtUtil jwtUtil;

    @Resource
    private final PasswordUtil passwordUtil;

    private final TokenUpdateService tokenUpdateService;

    @Value("${auth.max-login-attempts:5}")
    private int maxLoginAttempts;

    @Value("${auth.account-lock-duration-minutes:30}")
    private int accountLockDurationMinutes;

    @Value("${auth.max-active-tokens-per-user:10}")
    private int maxActiveTokensPerUser;

    @Value("${auth.allow-multiple-devices:true}")
    private boolean allowMultipleDevices;

    @Value("${auth.password-expire-days:90}")
    private int passwordExpireDays;

    @Value("${auth.password-expire-warning-days:7}")
    private int passwordExpireWarningDays;

    @Override
    @Transactional
    public LoginDTO login(LoginRequestDTO loginRequestDTO, String clientIp, String userAgent) {
        log.info("用户登录请求: username={}, deviceId={}, clientIp={}", 
                loginRequestDTO.getUsername(), loginRequestDTO.getDeviceId(), clientIp);

        try {
            UserEntity userEntity = validateUserCredentials(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
            if (userEntity == null) {
                recordLoginFailure(loginRequestDTO.getUsername(), clientIp, userAgent, "用户名或密码错误");
                throw new RuntimeException("用户名或密码错误");
            }

            checkAccountStatus(userEntity, loginRequestDTO.getUsername(), clientIp, userAgent);

            if (isSuspiciousLogin(userEntity.getId(), clientIp, userAgent)) {
                log.warn("检测到可疑登录: userId={}, clientIp={}, userAgent={}", userEntity.getId(), clientIp, userAgent);
                //TODO 这里可以添加额外的验证步骤，如发送验证码等
            }

            if (!allowMultipleDevices) {
                singleSignOnCheck(userEntity.getId(), loginRequestDTO.getDeviceId(), false);
            }

            String sessionId = IdUtil.fastSimpleUUID();
            String deviceId = StrUtil.isNotBlank(loginRequestDTO.getDeviceId()) ?
                    loginRequestDTO.getDeviceId() : IdUtil.fastSimpleUUID();

            List<String> roles = getUserRoles(userEntity.getId());
            List<String> permissions = getUserPermissions(roles);

            String accessToken = jwtUtil.generateAccessToken(
                    userEntity.getId(),
                    userEntity.getUsername(),
                    roles,
                    permissions,
                    deviceId,
                    clientIp,
                    sessionId
            );
            String refreshToken = jwtUtil.generateRefreshToken(
                    userEntity.getId(), userEntity.getUsername(), deviceId, sessionId
            );

            UserTokenEntity userTokenEntity = saveUserToken(userEntity, accessToken, refreshToken,
                    deviceId, loginRequestDTO.getDeviceType(), clientIp, userAgent, sessionId);

            recordLoginSuccess(userEntity, clientIp, userAgent, sessionId, userTokenEntity.getId().toString());

            updateUserLoginInfo(userEntity, clientIp);

            return buildLoginResponse(userEntity, accessToken, refreshToken, sessionId,
                    roles, permissions, deviceId, clientIp, userAgent, loginRequestDTO);

        } catch (Exception e) {
            log.error("用户登录失败: username={}, error={}", loginRequestDTO.getUsername(), e.getMessage());
            recordLoginFailure(loginRequestDTO.getUsername(), clientIp, userAgent, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean logout(LogoutRequestDTO logoutRequestDTO, String clientIp) {
        try {
            String accessToken = logoutRequestDTO.getAccessToken();
            
            if (jwtUtil.validateToken(accessToken)) {
                return false;
            }

            Long userId = jwtUtil.getUserIdFromToken(accessToken);
            String sessionId = jwtUtil.getSessionIdFromToken(accessToken);

            if (logoutRequestDTO.getLogoutAllDevices()) {
                revokeAllUserTokens(userId, userId, "用户主动登出所有设备");
                userLoginLogRepository.logoutAllSessionsByUserId(userId, LocalDateTime.now());
            } else {
                revokeToken(accessToken, userId, "用户主动登出");
                if (StrUtil.isNotBlank(logoutRequestDTO.getRefreshToken())) {
                    userTokenRepository.revokeTokenByAccessToken(logoutRequestDTO.getRefreshToken(),
                            LocalDateTime.now(), userId, "用户主动登出");
                }
                userLoginLogRepository.updateLogoutTimeBySessionId(sessionId, LocalDateTime.now());
            }

            log.info("用户登出成功: userId={}, sessionId={}, logoutAllDevices={}", 
                    userId, sessionId, logoutRequestDTO.getLogoutAllDevices());
            return true;

        } catch (Exception e) {
            log.error("用户登出失败: error={}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public LoginDTO refreshToken(RefreshTokenDTO refreshTokenDTO, String clientIp) {
        try {
            String refreshToken = refreshTokenDTO.getRefreshToken();
            
            if (jwtUtil.validateToken(refreshToken) || jwtUtil.isRefreshToken(refreshToken)) {
                throw new RuntimeException("无效的刷新令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            String sessionId = jwtUtil.getSessionIdFromToken(refreshToken);

            Optional<UserTokenEntity> tokenOpt = userTokenRepository.findByRefreshToken(refreshToken);
            if (tokenOpt.isEmpty() || !tokenOpt.get().isAvailable()) {
                throw new RuntimeException("刷新令牌已失效");
            }

            UserEntity userEntity = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            throw new RuntimeException("用户账户已被禁用");

            //TODO 获取最新的角色和权限

        } catch (Exception e) {
            log.error("刷新令牌失败: error={}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        try {
            if (jwtUtil.validateToken(accessToken) || !jwtUtil.isAccessToken(accessToken)) {
                return false;
            }

            Optional<UserTokenEntity> tokenOpt = userTokenRepository.findByAccessToken(accessToken);
            if (tokenOpt.isEmpty()) {
                return false;
            }

            UserTokenEntity userTokenEntity = tokenOpt.get();
            if (!userTokenEntity.isAvailable() || userTokenEntity.isAccessTokenExpired()) {
                return false;
            }

            tokenUpdateService.updateTokenLastUsedTime(accessToken);
            return true;

        } catch (Exception e) {
            log.error("验证访问令牌失败: error={}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        try {
            if (jwtUtil.validateToken(refreshToken) || jwtUtil.isRefreshToken(refreshToken)) {
                return false;
            }

            Optional<UserTokenEntity> tokenOpt = userTokenRepository.findByRefreshToken(refreshToken);
            return tokenOpt.isPresent() && tokenOpt.get().isAvailable() && !tokenOpt.get().isRefreshTokenExpired();

        } catch (Exception e) {
            log.error("验证刷新令牌失败: error={}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public int revokeAllUserTokens(Long userId, Long revokedBy, String reason) {
        return userTokenRepository.revokeAllTokensByUserId(userId, LocalDateTime.now(), revokedBy, reason);
    }

    @Override
    @Transactional
    public boolean revokeToken(String accessToken, Long revokedBy, String reason) {
        return userTokenRepository.revokeTokenByAccessToken(accessToken, LocalDateTime.now(), revokedBy, reason) > 0;
    }

    @Override
    @Transactional
    public int revokeUserDeviceTokens(Long userId, String deviceId, Long revokedBy, String reason) {
        return userTokenRepository.revokeTokensByUserIdAndDeviceId(userId, deviceId, LocalDateTime.now(), revokedBy, reason);
    }

    @Override
    public UserEntity getUserFromToken(String accessToken) {
        try {
            if (!validateAccessToken(accessToken)) {
                return null;
            }

            Long userId = jwtUtil.getUserIdFromToken(accessToken);
            return userRepository.findById(userId).orElse(null);

        } catch (Exception e) {
            log.error("从令牌获取用户信息失败: error={}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<String> getUserRolesFromToken(String accessToken) {
        try {
            if (!validateAccessToken(accessToken)) {
                return Collections.emptyList();
            }

            return jwtUtil.getRolesFromToken(accessToken);

        } catch (Exception e) {
            log.error("从令牌获取用户角色失败: error={}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getUserPermissionsFromToken(String accessToken) {
        try {
            if (!validateAccessToken(accessToken)) {
                return Collections.emptyList();
            }

            return jwtUtil.getPermissionsFromToken(accessToken);

        } catch (Exception e) {
            log.error("从令牌获取用户权限失败: error={}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public String generateTempToken(Long userId, String purpose, Map<String, Object> extraClaims, Integer expireMinutes) {
        try {
            UserEntity userEntity = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            return jwtUtil.generateTempToken(userId, userEntity.getUsername(), purpose, extraClaims);

        } catch (Exception e) {
            log.error("生成临时令牌失败: userId={}, error={}", userId, e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean validateTempToken(String tempToken, String purpose) {
        try {
            if (jwtUtil.validateToken(tempToken) || !jwtUtil.isTempToken(tempToken)) {
                return false;
            }

            Map<String, Object> claims = jwtUtil.getAllClaimsFromToken(tempToken);
            String tokenPurpose = (String) claims.get("purpose");
            return StrUtil.equals(purpose, tokenPurpose);

        } catch (Exception e) {
            log.error("验证临时令牌失败: error={}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isUserOnline(Long userId) {
        List<UserTokenEntity> activeTokens = userTokenRepository.findValidTokensByUserId(userId);
        return !activeTokens.isEmpty();
    }

    @Override
    public long getUserActiveTokenCount(Long userId) {
        return userTokenRepository.countActiveTokensByUserId(userId);
    }

    @Override
    public List<UserTokenEntity> getUserActiveTokens(Long userId) {
        return userTokenRepository.findValidTokensByUserId(userId);
    }

    @Override
    @Transactional
    public int forceUserOffline(Long userId, Long operatorId, String reason) {
        // 撤销所有令牌
        int revokedTokens = revokeAllUserTokens(userId, operatorId, reason);
        
        // 更新登录日志
        userLoginLogRepository.logoutAllSessionsByUserId(userId, LocalDateTime.now());
        
        log.info("强制用户下线: userId={}, operatorId={}, reason={}, revokedTokens={}", 
                userId, operatorId, reason, revokedTokens);
        return revokedTokens;
    }

    @Override
    public void singleSignOnCheck(Long userId, String deviceId, boolean allowMultipleDevices) {
        if (allowMultipleDevices) {
            return;
        }

        // 检查是否有其他设备的活跃令牌
        List<UserTokenEntity> activeTokens = userTokenRepository.findValidTokensByUserId(userId);
        List<UserTokenEntity> otherDeviceTokens = activeTokens.stream()
                .filter(token -> !StrUtil.equals(token.getDeviceId(), deviceId))
                .toList();

        if (!otherDeviceTokens.isEmpty()) {
            // 撤销其他设备的令牌
            for (UserTokenEntity token : otherDeviceTokens) {
                userTokenRepository.revokeTokenByAccessToken(token.getAccessToken(), 
                        LocalDateTime.now(), userId, "单点登录限制");
            }
        }
    }

    @Override
    @Transactional
    public void updateTokenLastUsedTime(String accessToken) {
        tokenUpdateService.updateTokenLastUsedTime(accessToken);
    }

    @Override
    @Transactional
    public int cleanupExpiredTokens() {
        // 标记过期令牌
        int markedExpired = userTokenRepository.markExpiredTokens();
        
        // 删除过期时间超过30天的令牌
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(30);
        int deletedExpired = userTokenRepository.deleteExpiredTokensBefore(cutoffTime);
        int deletedRevoked = userTokenRepository.deleteRevokedTokensBefore(cutoffTime);
        
        log.info("清理过期令牌完成: marked={}, deletedExpired={}, deletedRevoked={}", 
                markedExpired, deletedExpired, deletedRevoked);
        
        return markedExpired + deletedExpired + deletedRevoked;
    }

    @Override
    public Map<String, Object> getTokenStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long activeTokens = userTokenRepository.countActiveTokens();
        stats.put("activeTokens", activeTokens);
        
        stats.put("accessTokens", userTokenRepository.countActiveTokensByType(UserTokenEntity.TokenType.ACCESS));
        stats.put("refreshTokens", userTokenRepository.countActiveTokensByType(UserTokenEntity.TokenType.REFRESH));
        stats.put("tempTokens", userTokenRepository.countActiveTokensByType(UserTokenEntity.TokenType.TEMP));
        

        long accessTokenCount = userTokenRepository.countActiveTokensByType(UserTokenEntity.TokenType.ACCESS);
        stats.put("tokenPairs", accessTokenCount);
        
        stats.put("onlineUsers", userLoginLogRepository.countOnlineUsers());
        
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrow = today.plusDays(1);
        stats.put("todayLogins", userLoginLogRepository.countSuccessLoginsBetween(today, tomorrow));
        stats.put("todayActiveUsers", userLoginLogRepository.countTodayActiveUsers(today, tomorrow));
        
        return stats;
    }

    @Override
    public UserEntity validateUserCredentials(String username, String password) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .or(() -> userRepository.findByPhone(username))
                .orElse(null);

        if (userEntity != null && passwordUtil.verifyPassword(password, userEntity.getPassword())) {
            return userEntity;
        }
        return null;
    }

    @Override
    @Transactional
    public void recordLoginFailure(String username, String clientIp, String userAgent, String reason) {
        recordLoginFailureInternal(username, clientIp, userAgent, reason);
        
        // 增加失败登录尝试次数并检查是否需要锁定账户
        userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .or(() -> userRepository.findByPhone(username))
                .ifPresent(userEntity -> {
                    userRepository.incrementFailedLoginAttempts(userEntity.getId());
                    
                    // 检查是否需要锁定账户
                    if (userEntity.getFailedLoginAttempts() + 1 >= maxLoginAttempts) {
                        // 直接调用 repository 方法，避免事务自调用
                        LocalDateTime now = LocalDateTime.now();
                        userRepository.lockUserAccount(userEntity.getId(), 
                                now.plusMinutes(accountLockDurationMinutes), 
                                "登录失败次数过多", now);
                        log.warn("用户账户已锁定: userId={}, reason={}", userEntity.getId(), "登录失败次数过多");
                    }
                });
    }
    
    /**
     * 内部方法：记录登录失败日志（不包含事务逻辑）
     */
    private void recordLoginFailureInternal(String username, String clientIp, String userAgent, String reason) {
        UserLoginLogEntity loginLog = new UserLoginLogEntity();
        loginLog.setUsername(username);
        loginLog.setLoginTime(LocalDateTime.now());
        loginLog.setLoginIp(clientIp);
        loginLog.setUserAgent(userAgent);
        loginLog.setLoginStatus(UserLoginLogEntity.LoginStatus.FAILED);
        loginLog.setLoginMessage(reason);
        
        // 解析用户代理
        parseUserAgent(loginLog, userAgent);
        
        userLoginLogRepository.save(loginLog);
    }

    @Override
    @Transactional
    public void recordLoginSuccess(UserEntity userEntity, String clientIp, String userAgent, String sessionId, String tokenId) {
        UserLoginLogEntity loginLog = new UserLoginLogEntity();
        loginLog.setUserId(userEntity.getId());
        loginLog.setUsername(userEntity.getUsername());
        loginLog.setLoginTime(LocalDateTime.now());
        loginLog.setLoginIp(clientIp);
        loginLog.setUserAgent(userAgent);
        loginLog.setLoginStatus(UserLoginLogEntity.LoginStatus.SUCCESS);
        loginLog.setLoginMessage("登录成功");
        loginLog.setSessionId(sessionId);
        loginLog.setTokenId(tokenId);
        
        // 解析用户代理
        parseUserAgent(loginLog, userAgent);
        
        userLoginLogRepository.save(loginLog);
    }

    @Override
    public boolean isAccountLocked(String username) {
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .or(() -> userRepository.findByPhone(username))
                .map(userEntity -> {
                    if (!Boolean.TRUE.equals(userEntity.getAccountLocked())) {
                        return false;
                    }
                    if (userEntity.getLockTime() == null) {
                        return true;
                    }
                    LocalDateTime lockDateTime = userEntity.getLockTime().toInstant()
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                    return lockDateTime.plusHours(1).isAfter(LocalDateTime.now());
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public void lockUserAccount(Long userId, String reason, Long operatorId) {
        LocalDateTime now = LocalDateTime.now();
        userRepository.lockUserAccount(userId, now.plusMinutes(accountLockDurationMinutes), reason, now);
        log.warn("用户账户已锁定: userId={}, reason={}, operatorId={}", userId, reason, operatorId);
    }

    @Override
    @Transactional
    public void unlockUserAccount(Long userId, Long operatorId) {
        userRepository.unlockUserAccount(userId, LocalDateTime.now());
        log.info("用户账户已解锁: userId={}, operatorId={}", userId, operatorId);
    }

    @Override
    public List<Map<String, Object>> getUserRecentLogins(Long userId, int limit) {
        List<UserLoginLogEntity> logs = userLoginLogRepository.findRecentSuccessLoginsByUserId(
                userId, PageRequest.of(0, limit));
        
        return logs.stream().map(log -> {
            Map<String, Object> loginInfo = new HashMap<>();
            loginInfo.put("loginTime", log.getLoginTime());
            loginInfo.put("loginIp", log.getLoginIp());
            loginInfo.put("loginLocation", log.getLoginLocation());
            loginInfo.put("loginDevice", log.getLoginDevice());
            loginInfo.put("loginBrowser", log.getLoginBrowser());
            loginInfo.put("loginOs", log.getLoginOs());
            return loginInfo;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean isSuspiciousLogin(Long userId, String clientIp, String userAgent) {
        // 检查最近7天的登录记录
        LocalDateTime checkTime = LocalDateTime.now().minusDays(7);
        List<UserLoginLogEntity> suspiciousLogins = userLoginLogRepository.findSuspiciousLogins(userId, checkTime);
        
        return !suspiciousLogins.isEmpty();
    }

    private void checkAccountStatus(UserEntity userEntity, String username, String clientIp, String userAgent) {
        if (!"ACTIVE".equals(userEntity.getUserStatus().name())) {
            recordLoginFailureInternal(username, clientIp, userAgent, "账户已被禁用");
            throw new RuntimeException("账户已被禁用");
        }
        
        if (Boolean.TRUE.equals(userEntity.getAccountLocked())) {
            recordLoginFailureInternal(username, clientIp, userAgent, "账户已被锁定");
            throw new RuntimeException("账户已被锁定");
        }
    }

    private List<String> getUserRoles(Long userId) {
        return roleRepository.findRoleCodesByUserId(userId);
    }

    private List<String> getUserPermissions(List<String> roles) {
        // TODO 这里应该根据角色获取权限，暂时返回空列表
        return new ArrayList<>();
    }

    private UserTokenEntity saveUserToken(UserEntity userEntity, String accessToken, String refreshToken,
                                          String deviceId, String deviceType, String clientIp,
                                          String userAgent, String sessionId) {
        UserTokenEntity userTokenEntity = new UserTokenEntity();
        userTokenEntity.setUserId(userEntity.getId());
        userTokenEntity.setAccessToken(accessToken);
        userTokenEntity.setRefreshToken(refreshToken);
        userTokenEntity.setTokenType(UserTokenEntity.TokenType.ACCESS);
        userTokenEntity.setExpireTime(jwtUtil.getExpirationFromToken(accessToken));
        userTokenEntity.setRefreshExpireTime(jwtUtil.getExpirationFromToken(refreshToken));
        userTokenEntity.setClientIp(clientIp);
        userTokenEntity.setUserAgent(userAgent);
        userTokenEntity.setDeviceId(deviceId);
        userTokenEntity.setDeviceType(deviceType);
        userTokenEntity.setTokenStatus(UserTokenEntity.TokenStatus.ACTIVE);
        userTokenEntity.setLastUsedTime(LocalDateTime.now());
        
        return userTokenRepository.save(userTokenEntity);
    }

    private void updateUserLoginInfo(UserEntity userEntity, String clientIp) {
        userRepository.updateLoginInfo(userEntity.getId(), LocalDateTime.now(), clientIp);
        userRepository.resetFailedLoginAttempts(userEntity.getId());
    }

    private LoginDTO buildLoginResponse(UserEntity userEntity, String accessToken, String refreshToken,
                                        String sessionId, List<String> roles, List<String> permissions,
                                        String deviceId, String clientIp, String userAgent,
                                        LoginRequestDTO loginRequestDTO) {
        LoginDTO.UserInfo userInfo = LoginDTO.UserInfo.builder()
                .userId(userEntity.getId())
                .username(userEntity.getUsername())
                .nickname(userEntity.getNickname())
                .realName(userEntity.getRealName())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .avatarUrl(userEntity.getAvatar())
                .status(userEntity.getUserStatus())
                .createTime(userEntity.getCreateTime())
                .build();

        LoginDTO.PermissionInfo permissionInfo = LoginDTO.PermissionInfo.builder()
                .roles(roles)
                .permissions(permissions)
                .isAdmin(roles.contains("ADMIN") || roles.contains("SUPER_ADMIN"))
                .build();

        LoginDTO.DeviceInfo deviceInfo = LoginDTO.DeviceInfo.builder()
                .deviceId(deviceId)
                .deviceType(loginRequestDTO.getDeviceType())
                .deviceName(loginRequestDTO.getDeviceName())
                .clientIp(clientIp)
                .build();

        if (StrUtil.isNotBlank(userAgent)) {
            UserAgent ua = UserAgentUtil.parse(userAgent);
            deviceInfo.setOs(ua.getOs().getName());
            deviceInfo.setBrowser(ua.getBrowser().getName());
        }

        boolean needChangePassword = userEntity.getPasswordChangeTime() == null;
        boolean passwordExpiringSoon = false;
        Integer passwordExpiringDays = null;
        
        if (userEntity.getPasswordChangeTime() != null) {
            Date expireTime = DateUtil.offsetDay(userEntity.getPasswordChangeTime(), passwordExpireDays);
            Date warningTime = DateUtil.offsetDay(expireTime, -passwordExpireWarningDays);
            
            if (DateUtil.compare(new Date(), warningTime) > 0) {
                passwordExpiringSoon = true;
                passwordExpiringDays = (int) DateUtil.betweenDay(new Date(), expireTime, false);
            }
        }

        return LoginDTO.success(
                accessToken, 
                refreshToken, 
                jwtUtil.getTokenRemainingTime(accessToken),
                jwtUtil.getTokenRemainingTime(refreshToken)
        )
        .withUserInfo(userInfo)
        .withPermissions(permissionInfo)
        .withDeviceInfo(deviceInfo)
        .withSessionInfo(sessionId, userEntity.getLastLoginTime(), userEntity.getLastLoginIp())
        .withPasswordStatus(needChangePassword, passwordExpiringSoon, passwordExpiringDays);
    }

    private void parseUserAgent(UserLoginLogEntity loginLog, String userAgent) {
        if (StrUtil.isNotBlank(userAgent)) {
            try {
                UserAgent ua = UserAgentUtil.parse(userAgent);
                loginLog.setLoginBrowser(ua.getBrowser().getName());
                loginLog.setLoginOs(ua.getOs().getName());
                loginLog.setLoginDevice(ua.getPlatform().getName());
            } catch (Exception e) {
                log.warn("解析用户代理失败: {}", e.getMessage());
            }
        }
    }
}
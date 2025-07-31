package com.loveyue.auth.repository;

import com.loveyue.auth.entity.UserTokenEntity;
import com.loveyue.common.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 用户Token仓库接口
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Repository
public interface UserTokenRepository extends PagingAndSortingRepository<UserTokenEntity, Long> , CrudRepository<UserTokenEntity, Long> {

    /**
     * 根据访问令牌查找Token记录
     */
    Optional<UserTokenEntity> findByAccessToken(String accessToken);

    /**
     * 根据刷新令牌查找Token记录
     */
    Optional<UserTokenEntity> findByRefreshToken(String refreshToken);

    /**
     * 根据用户ID查找Token列表
     */
    List<UserTokenEntity> findByUserIdOrderByCreateTimeDesc(Long userId);

    /**
     * 根据用户ID和设备ID查找Token
     */
    Optional<UserTokenEntity> findByUserIdAndDeviceId(Long userId, String deviceId);

    /**
     * 根据用户ID查找有效的Token列表
     */
    @Query("SELECT ut FROM UserTokenEntity ut WHERE ut.userId = :userId AND " +
           "ut.tokenStatus = 'ACTIVE' AND ut.expireTime > CURRENT_TIMESTAMP " +
           "ORDER BY ut.createTime DESC")
    List<UserTokenEntity> findValidTokensByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和Token类型查找有效Token
     */
    @Query("SELECT ut FROM UserTokenEntity ut WHERE ut.userId = :userId AND " +
           "ut.tokenType = :tokenType AND ut.tokenStatus = 'ACTIVE' AND " +
           "ut.expireTime > CURRENT_TIMESTAMP ORDER BY ut.createTime DESC")
    List<UserTokenEntity> findValidTokensByUserIdAndType(@Param("userId") Long userId,
                                                         @Param("tokenType") UserTokenEntity.TokenType tokenType);

    /**
     * 查找过期的Token
     */
    @Query("SELECT ut FROM UserTokenEntity ut WHERE ut.expireTime <= CURRENT_TIMESTAMP AND ut.tokenStatus = 'ACTIVE'")
    List<UserTokenEntity> findExpiredTokens();

    /**
     * 查找即将过期的Token（指定时间内）
     */
    @Query("SELECT ut FROM UserTokenEntity ut WHERE ut.expireTime > CURRENT_TIMESTAMP AND " +
           "ut.expireTime <= :expireTime AND ut.tokenStatus = 'ACTIVE'")
    List<UserTokenEntity> findExpiringTokens(@Param("expireTime") LocalDateTime expireTime);

    /**
     * 查找刷新令牌过期的Token
     */
    @Query("SELECT ut FROM UserTokenEntity ut WHERE ut.refreshExpireTime <= CURRENT_TIMESTAMP AND ut.tokenStatus = 'ACTIVE'")
    List<UserTokenEntity> findRefreshExpiredTokens();

    /**
     * 分页查询Token记录
     */
    @Query("SELECT ut FROM UserTokenEntity ut WHERE " +
           "(:userId IS NULL OR ut.userId = :userId) AND " +
           "(:tokenType IS NULL OR ut.tokenType = :tokenType) AND " +
           "(:tokenStatus IS NULL OR ut.tokenStatus = :tokenStatus) AND " +
           "(:deviceType IS NULL OR ut.deviceType LIKE %:deviceType%) AND " +
           "(:clientIp IS NULL OR ut.clientIp = :clientIp) AND " +
           "(:startTime IS NULL OR ut.createTime >= :startTime) AND " +
           "(:endTime IS NULL OR ut.createTime <= :endTime) " +
           "ORDER BY ut.createTime DESC")
    Page<UserTokenEntity> findTokensWithConditions(
            @Param("userId") Long userId,
            @Param("tokenType") UserTokenEntity.TokenType tokenType,
            @Param("tokenStatus") UserTokenEntity.TokenStatus tokenStatus,
            @Param("deviceType") String deviceType,
            @Param("clientIp") String clientIp,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    /**
     * 根据客户端IP查找Token
     */
    List<UserTokenEntity> findByClientIpOrderByCreateTimeDesc(String clientIp);

    /**
     * 根据设备类型查找Token
     */
    List<UserTokenEntity> findByDeviceTypeOrderByCreateTimeDesc(String deviceType);

    /**
     * 查找长时间未使用的Token
     */
    @Query("SELECT ut FROM UserTokenEntity ut WHERE ut.lastUsedTime < :lastUsedTime AND ut.tokenStatus = 'ACTIVE'")
    List<UserTokenEntity> findUnusedTokens(@Param("lastUsedTime") LocalDateTime lastUsedTime);

    /**
     * 撤销用户的所有Token
     */
    @Modifying
    @Query("UPDATE UserTokenEntity ut SET ut.tokenStatus = 'REVOKED', ut.revokedTime = :revokedTime, " +
           "ut.revokedBy = :revokedBy, ut.revokeReason = :revokeReason WHERE ut.userId = :userId AND ut.tokenStatus = 'ACTIVE'")
    int revokeAllTokensByUserId(@Param("userId") Long userId, 
                               @Param("revokedTime") LocalDateTime revokedTime,
                               @Param("revokedBy") Long revokedBy,
                               @Param("revokeReason") String revokeReason);

    /**
     * 撤销指定Token
     */
    @Modifying
    @Query("UPDATE UserTokenEntity ut SET ut.tokenStatus = 'REVOKED', ut.revokedTime = :revokedTime, " +
           "ut.revokedBy = :revokedBy, ut.revokeReason = :revokeReason WHERE ut.accessToken = :accessToken")
    int revokeTokenByAccessToken(@Param("accessToken") String accessToken,
                                @Param("revokedTime") LocalDateTime revokedTime,
                                @Param("revokedBy") Long revokedBy,
                                @Param("revokeReason") String revokeReason);

    /**
     * 撤销用户指定设备的Token
     */
    @Modifying
    @Query("UPDATE UserTokenEntity ut SET ut.tokenStatus = 'REVOKED', ut.revokedTime = :revokedTime, " +
           "ut.revokedBy = :revokedBy, ut.revokeReason = :revokeReason " +
           "WHERE ut.userId = :userId AND ut.deviceId = :deviceId AND ut.tokenStatus = 'ACTIVE'")
    int revokeTokensByUserIdAndDeviceId(@Param("userId") Long userId,
                                       @Param("deviceId") String deviceId,
                                       @Param("revokedTime") LocalDateTime revokedTime,
                                       @Param("revokedBy") Long revokedBy,
                                       @Param("revokeReason") String revokeReason);

    /**
     * 更新Token最后使用时间
     */
    @Modifying
    @Query("UPDATE UserTokenEntity ut SET ut.lastUsedTime = :lastUsedTime WHERE ut.accessToken = :accessToken")
    int updateLastUsedTime(@Param("accessToken") String accessToken, @Param("lastUsedTime") LocalDateTime lastUsedTime);

    /**
     * 批量标记过期Token为过期状态
     */
    @Modifying
    @Query("UPDATE UserTokenEntity ut SET ut.tokenStatus = 'EXPIRED' WHERE ut.expireTime <= CURRENT_TIMESTAMP AND ut.tokenStatus = 'ACTIVE'")
    int markExpiredTokens();

    /**
     * 删除指定时间之前的已撤销Token
     */
    @Modifying
    @Query("DELETE FROM UserTokenEntity ut WHERE ut.revokedTime < :beforeTime AND ut.tokenStatus = 'REVOKED'")
    int deleteRevokedTokensBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 删除指定时间之前的过期Token
     */
    @Modifying
    @Query("DELETE FROM UserTokenEntity ut WHERE ut.expireTime < :beforeTime AND ut.tokenStatus = 'EXPIRED'")
    int deleteExpiredTokensBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 统计用户的活跃Token数量
     */
    @Query("SELECT COUNT(ut) FROM UserTokenEntity ut WHERE ut.userId = :userId AND ut.tokenStatus = 'ACTIVE' AND ut.expireTime > CURRENT_TIMESTAMP")
    long countActiveTokensByUserId(@Param("userId") Long userId);

    /**
     * 统计指定时间范围内创建的Token数量
     */
    @Query("SELECT COUNT(ut) FROM UserTokenEntity ut WHERE ut.createTime >= :startTime AND ut.createTime <= :endTime")
    long countTokensCreatedBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计当前活跃Token总数
     */
    @Query("SELECT COUNT(ut) FROM UserTokenEntity ut WHERE ut.tokenStatus = 'ACTIVE' AND ut.expireTime > CURRENT_TIMESTAMP")
    long countActiveTokens();

    /**
     * 根据Token类型统计数量
     */
    @Query("SELECT COUNT(ut) FROM UserTokenEntity ut WHERE ut.tokenType = :tokenType AND ut.tokenStatus = 'ACTIVE' AND ut.expireTime > CURRENT_TIMESTAMP")
    long countActiveTokensByType(@Param("tokenType") UserTokenEntity.TokenType tokenType);
}
package com.loveyue.auth.repository;

import com.loveyue.auth.entity.UserLoginLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户登录日志仓库接口
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Repository
public interface UserLoginLogRepository extends PagingAndSortingRepository<UserLoginLogEntity, Long>, CrudRepository<UserLoginLogEntity, Long> {

    /**
     * 根据用户ID查找登录日志
     */
    List<UserLoginLogEntity> findByUserIdOrderByLoginTimeDesc(Long userId);

    /**
     * 根据用户名查找登录日志
     */
    List<UserLoginLogEntity> findByUsernameOrderByLoginTimeDesc(String username);

    /**
     * 根据会话ID查找登录日志
     */
    Optional<UserLoginLogEntity> findBySessionId(String sessionId);

    /**
     * 根据Token ID查找登录日志
     */
    Optional<UserLoginLogEntity> findByTokenId(String tokenId);

    /**
     * 分页查询用户登录日志
     */
    @Query("SELECT ull FROM UserLoginLogEntity ull WHERE " +
           "(:userId IS NULL OR ull.userId = :userId) AND " +
           "(:username IS NULL OR ull.username LIKE %:username%) AND " +
           "(:loginIp IS NULL OR ull.loginIp = :loginIp) AND " +
           "(:loginStatus IS NULL OR ull.loginStatus = :loginStatus) AND " +
           "(:startTime IS NULL OR ull.loginTime >= :startTime) AND " +
           "(:endTime IS NULL OR ull.loginTime <= :endTime) " +
           "ORDER BY ull.loginTime DESC")
    Page<UserLoginLogEntity> findLoginLogsWithConditions(
            @Param("userId") Long userId,
            @Param("username") String username,
            @Param("loginIp") String loginIp,
            @Param("loginStatus") UserLoginLogEntity.LoginStatus loginStatus,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    /**
     * 查找用户最近的成功登录记录
     */
    @Query("SELECT ull FROM UserLoginLogEntity ull WHERE ull.userId = :userId AND " +
           "ull.loginStatus = 'SUCCESS' ORDER BY ull.loginTime DESC")
    List<UserLoginLogEntity> findRecentSuccessLoginsByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 查找用户最近的失败登录记录
     */
    @Query("SELECT ull FROM UserLoginLogEntity ull WHERE ull.userId = :userId AND " +
           "ull.loginStatus = 'FAILED' ORDER BY ull.loginTime DESC")
    List<UserLoginLogEntity> findRecentFailedLoginsByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 查找指定时间范围内的登录记录
     */
    @Query("SELECT ull FROM UserLoginLogEntity ull WHERE ull.loginTime >= :startTime AND ull.loginTime <= :endTime " +
           "ORDER BY ull.loginTime DESC")
    List<UserLoginLogEntity> findLoginLogsBetween(@Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 查找在线用户（未退出的登录记录）
     */
    @Query("SELECT ull FROM UserLoginLogEntity ull WHERE ull.logoutTime IS NULL AND " +
           "ull.loginStatus = 'SUCCESS' ORDER BY ull.loginTime DESC")
    List<UserLoginLogEntity> findOnlineUsers();

    /**
     * 查找指定用户的在线会话
     */
    @Query("SELECT ull FROM UserLoginLogEntity ull WHERE ull.userId = :userId AND ull.logoutTime IS NULL AND " +
           "ull.loginStatus = 'SUCCESS' ORDER BY ull.loginTime DESC")
    List<UserLoginLogEntity> findOnlineSessionsByUserId(@Param("userId") Long userId);

    /**
     * 根据IP地址查找登录记录
     */
    List<UserLoginLogEntity> findByLoginIpOrderByLoginTimeDesc(String loginIp);

    /**
     * 查找可疑登录（异常IP或设备）
     */
    @Query("SELECT ull FROM UserLoginLogEntity ull WHERE ull.userId = :userId AND " +
           "(ull.loginIp NOT IN (SELECT DISTINCT ull2.loginIp FROM UserLoginLogEntity ull2 WHERE ull2.userId = :userId AND ull2.loginStatus = 'SUCCESS' AND ull2.loginTime < :checkTime) OR " +
           "ull.loginDevice NOT IN (SELECT DISTINCT ull3.loginDevice FROM UserLoginLogEntity ull3 WHERE ull3.userId = :userId AND ull3.loginStatus = 'SUCCESS' AND ull3.loginTime < :checkTime)) AND " +
           "ull.loginTime >= :checkTime ORDER BY ull.loginTime DESC")
    List<UserLoginLogEntity> findSuspiciousLogins(@Param("userId") Long userId, @Param("checkTime") LocalDateTime checkTime);

    /**
     * 统计用户登录次数
     */
    @Query("SELECT COUNT(ull) FROM UserLoginLogEntity ull WHERE ull.userId = :userId AND ull.loginStatus = 'SUCCESS'")
    long countSuccessLoginsByUserId(@Param("userId") Long userId);

    /**
     * 统计用户失败登录次数
     */
    @Query("SELECT COUNT(ull) FROM UserLoginLogEntity ull WHERE ull.userId = :userId AND ull.loginStatus = 'FAILED'")
    long countFailedLoginsByUserId(@Param("userId") Long userId);

    /**
     * 统计指定时间范围内的登录次数
     */
    @Query("SELECT COUNT(ull) FROM UserLoginLogEntity ull WHERE ull.loginTime >= :startTime AND ull.loginTime <= :endTime AND ull.loginStatus = 'SUCCESS'")
    long countSuccessLoginsBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计今日登录用户数
     */
    @Query("SELECT COUNT(DISTINCT ull.userId) FROM UserLoginLogEntity ull WHERE ull.loginTime >= :startOfDay AND ull.loginTime < :endOfDay AND ull.loginStatus = 'SUCCESS'")
    long countTodayActiveUsers(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * 统计在线用户数
     */
    @Query("SELECT COUNT(DISTINCT ull.userId) FROM UserLoginLogEntity ull WHERE ull.logoutTime IS NULL AND ull.loginStatus = 'SUCCESS'")
    long countOnlineUsers();

    /**
     * 更新退出时间
     */
    @Modifying
    @Query("UPDATE UserLoginLogEntity ull SET ull.logoutTime = :logoutTime WHERE ull.sessionId = :sessionId")
    int updateLogoutTimeBySessionId(@Param("sessionId") String sessionId, @Param("logoutTime") LocalDateTime logoutTime);

    /**
     * 更新退出时间（根据Token ID）
     */
    @Modifying
    @Query("UPDATE UserLoginLogEntity ull SET ull.logoutTime = :logoutTime WHERE ull.tokenId = :tokenId")
    int updateLogoutTimeByTokenId(@Param("tokenId") String tokenId, @Param("logoutTime") LocalDateTime logoutTime);

    /**
     * 批量更新用户的所有在线会话为退出状态
     */
    @Modifying
    @Query("UPDATE UserLoginLogEntity ull SET ull.logoutTime = :logoutTime WHERE ull.userId = :userId AND ull.logoutTime IS NULL")
    int logoutAllSessionsByUserId(@Param("userId") Long userId, @Param("logoutTime") LocalDateTime logoutTime);

    /**
     * 删除指定时间之前的登录日志
     */
    @Modifying
    @Query("DELETE FROM UserLoginLogEntity ull WHERE ull.loginTime < :beforeTime")
    int deleteLoginLogsBefore(@Param("beforeTime") LocalDateTime beforeTime);
}
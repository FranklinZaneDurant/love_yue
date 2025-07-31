package com.loveyue.auth.repository;

import com.loveyue.auth.entity.UserEntity;
import com.loveyue.common.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户仓库接口
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

    Optional<UserEntity> findById(Long id);

    /**
     * 根据用户名查找用户
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * 根据手机号查找用户
     */
    Optional<UserEntity> findByPhone(String phone);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 根据部门ID查找用户列表
     */
    List<UserEntity> findByDepartmentId(Long departmentId);

    /**
     * 根据用户状态查找用户列表
     */
    List<UserEntity> findByUserStatus(UserStatus userStatus);

    /**
     * 分页查询用户
     */
    @Query("SELECT u FROM UserEntity u WHERE " +
           "(:username IS NULL OR u.username LIKE %:username%) AND " +
           "(:email IS NULL OR u.email LIKE %:email%) AND " +
           "(:phone IS NULL OR u.phone LIKE %:phone%) AND " +
           "(:userStatus IS NULL OR u.userStatus = :userStatus) AND " +
           "(:departmentId IS NULL OR u.departmentId = :departmentId) AND " +
           "u.deleted = false")
    Page<UserEntity> findUsersWithConditions(
            @Param("username") String username,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("userStatus") UserStatus userStatus,
            @Param("departmentId") Long departmentId,
            Pageable pageable);

    /**
     * 查找锁定的用户
     */
    @Query("SELECT u FROM UserEntity u WHERE u.accountLocked = true AND u.deleted = false")
    List<UserEntity> findLockedUsers();

    /**
     * 查找需要解锁的用户（锁定时间超过指定时间）
     */
    @Query("SELECT u FROM UserEntity u WHERE u.accountLocked = true AND u.lockTime < :unlockTime AND u.deleted = false")
    List<UserEntity> findUsersToUnlock(@Param("unlockTime") LocalDateTime unlockTime);

    /**
     * 查找长时间未登录的用户
     */
    @Query("SELECT u FROM UserEntity u WHERE u.lastLoginTime < :lastLoginTime AND u.deleted = false")
    List<UserEntity> findInactiveUsers(@Param("lastLoginTime") LocalDateTime lastLoginTime);

    /**
     * 批量更新用户状态
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.userStatus = :status, u.lastModifiedTime = :modifiedTime, u.lastModifiedBy = :modifiedBy WHERE u.id IN :userIds")
    int updateUserStatus(@Param("userIds") List<Long> userIds, 
                        @Param("status") UserStatus status,
                        @Param("modifiedTime") LocalDateTime modifiedTime,
                        @Param("modifiedBy") Long modifiedBy);

    /**
     * 批量解锁用户
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.accountLocked = false, u.lockTime = null, u.failedLoginAttempts = 0, " +
           "u.lastModifiedTime = :modifiedTime, u.lastModifiedBy = :modifiedBy WHERE u.id IN :userIds")
    int unlockUsers(@Param("userIds") List<Long> userIds,
                   @Param("modifiedTime") LocalDateTime modifiedTime,
                   @Param("modifiedBy") Long modifiedBy);

    /**
     * 更新用户登录信息
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.lastLoginTime = :loginTime, u.lastLoginIp = :loginIp, " +
           "u.loginCount = u.loginCount + 1, u.failedLoginAttempts = 0 WHERE u.id = :userId")
    void updateLoginInfo(@Param("userId") Long userId,
                       @Param("loginTime") LocalDateTime loginTime,
                       @Param("loginIp") String loginIp);

    /**
     * 增加失败登录尝试次数
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.failedLoginAttempts = u.failedLoginAttempts + 1 WHERE u.id = :userId")
    void incrementFailedLoginAttempts(@Param("userId") Long userId);

    /**
     * 重置失败登录尝试次数
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.failedLoginAttempts = 0 WHERE u.id = :userId")
    void resetFailedLoginAttempts(@Param("userId") Long userId);

    /**
     * 锁定用户账户
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.accountLocked = true, u.lockTime = :lockTime WHERE u.id = :userId")
    int lockUser(@Param("userId") Long userId, @Param("lockTime") LocalDateTime lockTime);

    /**
     * 锁定用户账户（带原因）
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.accountLocked = true, u.lockTime = :lockTime, u.lockReason = :reason, " +
           "u.lastModifiedTime = :modifiedTime WHERE u.id = :userId")
    void lockUserAccount(@Param("userId") Long userId,
                       @Param("lockTime") LocalDateTime lockTime,
                       @Param("reason") String reason,
                       @Param("modifiedTime") LocalDateTime modifiedTime);

    /**
     * 解锁用户账户
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.accountLocked = false, u.lockTime = null, u.lockReason = null, " +
           "u.failedLoginAttempts = 0, u.lastModifiedTime = :modifiedTime WHERE u.id = :userId")
    void unlockUserAccount(@Param("userId") Long userId, @Param("modifiedTime") LocalDateTime modifiedTime);

    /**
     * 统计用户数量
     */
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.deleted = false")
    long countActiveUsers();

    /**
     * 统计今日新增用户数量
     */
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.createTime >= :startTime AND u.createTime < :endTime")
    long countTodayNewUsers(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
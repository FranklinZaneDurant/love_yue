package com.loveyue.auth.repository;

import com.loveyue.auth.entity.UserRoleEntity;
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
 * 用户角色关联仓库接口
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Repository
public interface UserRoleRepository extends PagingAndSortingRepository<UserRoleEntity, Long> , CrudRepository<UserRoleEntity, Long> {

    /**
     * 根据用户ID和角色ID查找关联关系
     */
    Optional<UserRoleEntity> findByUserIdAndRoleId(Long userId, Long roleId);

    /**
     * 根据用户ID查找角色关联列表
     */
    List<UserRoleEntity> findByUserId(Long userId);

    /**
     * 根据角色ID查找用户关联列表
     */
    List<UserRoleEntity> findByRoleId(Long roleId);

    /**
     * 根据用户ID查找有效的角色关联列表
     */
    @Query("SELECT e FROM UserRoleEntity e WHERE e.userId = :userId AND " +
           "(e.expireTime IS NULL OR e.expireTime > CURRENT_TIMESTAMP)")
    List<UserRoleEntity> findValidRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查找有效的用户关联列表
     */
    @Query("SELECT e FROM UserRoleEntity e WHERE e.roleId = :roleId AND " +
           "(e.expireTime IS NULL OR e.expireTime > CURRENT_TIMESTAMP)")
    List<UserRoleEntity> findValidUsersByRoleId(@Param("roleId") Long roleId);

    /**
     * 检查用户是否拥有指定角色
     */
    @Query("SELECT COUNT(e) > 0 FROM UserRoleEntity e WHERE e.userId = :userId AND e.roleId = :roleId AND " +
           "(e.expireTime IS NULL OR e.expireTime > CURRENT_TIMESTAMP)")
    boolean existsValidUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 查找过期的角色关联
     */
    @Query("SELECT e FROM UserRoleEntity e WHERE e.expireTime IS NOT NULL AND e.expireTime <= CURRENT_TIMESTAMP")
    List<UserRoleEntity> findExpiredUserRoles();

    /**
     * 查找即将过期的角色关联（指定天数内）
     */
    @Query("SELECT e FROM UserRoleEntity e WHERE e.expireTime IS NOT NULL AND " +
           "e.expireTime > CURRENT_TIMESTAMP AND e.expireTime <= :expireTime")
    List<UserRoleEntity> findExpiringUserRoles(@Param("expireTime") LocalDateTime expireTime);

    /**
     * 删除用户的所有角色关联
     */
    @Modifying
    @Query("DELETE FROM UserRoleEntity e WHERE e.userId = :userId")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除角色的所有用户关联
     */
    @Modifying
    @Query("DELETE FROM UserRoleEntity e WHERE e.roleId = :roleId")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除指定用户的指定角色关联
     */
    @Modifying
    @Query("DELETE FROM UserRoleEntity e WHERE e.userId = :userId AND e.roleId = :roleId")
    int deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 批量删除用户角色关联
     */
    @Modifying
    @Query("DELETE FROM UserRoleEntity e WHERE e.userId = :userId AND e.roleId IN :roleIds")
    int deleteByUserIdAndRoleIds(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * 删除过期的角色关联
     */
    @Modifying
    @Query("DELETE FROM UserRoleEntity e WHERE e.expireTime IS NOT NULL AND e.expireTime <= CURRENT_TIMESTAMP")
    int deleteExpiredUserRoles();

    /**
     * 统计用户的角色数量
     */
    @Query("SELECT COUNT(e) FROM UserRoleEntity e WHERE e.userId = :userId AND " +
           "(e.expireTime IS NULL OR e.expireTime > CURRENT_TIMESTAMP)")
    long countValidRolesByUserId(@Param("userId") Long userId);

    /**
     * 统计角色的用户数量
     */
    @Query("SELECT COUNT(e) FROM UserRoleEntity e WHERE e.roleId = :roleId AND " +
           "(e.expireTime IS NULL OR e.expireTime > CURRENT_TIMESTAMP)")
    long countValidUsersByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据授权人查找角色关联
     */
    List<UserRoleEntity> findByGrantedBy(Long grantedBy);

    /**
     * 根据授权时间范围查找角色关联
     */
    @Query("SELECT e FROM UserRoleEntity e WHERE e.grantedTime >= :startTime AND e.grantedTime <= :endTime")
    List<UserRoleEntity> findByGrantedTimeBetween(@Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);
}
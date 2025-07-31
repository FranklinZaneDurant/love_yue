package com.loveyue.auth.repository;

import com.loveyue.auth.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色仓库接口
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Repository
public interface RoleRepository extends PagingAndSortingRepository<RoleEntity, Long> {

    /**
     * 根据角色编码查找角色
     */
    Optional<RoleEntity> findByRoleCode(String roleCode);

    /**
     * 检查角色编码是否存在
     */
    boolean existsByRoleCode(String roleCode);

    /**
     * 查找系统角色
     */
    List<RoleEntity> findByIsSystemTrueOrderBySortOrder();

    /**
     * 分页查询角色
     */
    @Query("SELECT r FROM RoleEntity r WHERE " +
           "(:roleCode IS NULL OR r.roleCode LIKE %:roleCode%) AND " +
           "(:roleName IS NULL OR r.roleName LIKE %:roleName%) AND " +
           "(:isSystem IS NULL OR r.isSystem = :isSystem) AND " +
           "r.deleted = false ORDER BY r.sortOrder")
    Page<RoleEntity> findRolesWithConditions(
            @Param("roleCode") String roleCode,
            @Param("roleName") String roleName,
            @Param("isSystem") Boolean isSystem,
            Pageable pageable);

    /**
     * 根据用户ID查找角色列表
     */
    @Query("SELECT r FROM RoleEntity r INNER JOIN UserRoleEntity ur ON r.id = ur.roleId " +
           "WHERE ur.userId = :userId AND r.deleted = false AND " +
           "(ur.expireTime IS NULL OR ur.expireTime > CURRENT_TIMESTAMP) " +
           "ORDER BY r.sortOrder")
    List<RoleEntity> findRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查找角色编码列表
     */
    @Query("SELECT r.roleCode FROM RoleEntity r INNER JOIN UserRoleEntity ur ON r.id = ur.roleId " +
           "WHERE ur.userId = :userId AND r.deleted = false AND " +
           "(ur.expireTime IS NULL OR ur.expireTime > CURRENT_TIMESTAMP)")
    List<String> findRoleCodesByUserId(@Param("userId") Long userId);

    /**
     * 统计角色数量
     */
    @Query("SELECT COUNT(r) FROM RoleEntity r WHERE r.deleted = false")
    long countActiveRoles();


}
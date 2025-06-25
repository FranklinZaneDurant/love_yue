package com.loveyue.common.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import com.loveyue.common.uitls.DataTypeUtil;

import java.util.*;

/**
 * @Description: 数据仓库抽象类
 * @Date 2025/6/19
 * @Author LoveYue
 */
@SuppressWarnings("unused")
public abstract class BaseRepository {

    // 数据实体管理器
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 获取数据实体管理器
     *
     * @return 数据实体管理器
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected static class SQLQueryBuilder<T> {

        // 实体管理器
        private final EntityManager entityManager;

        // 查询条件构建器
        private final CriteriaBuilder criteriaBuilder;

        // 总数条件查询
        private final CriteriaQuery<Long> countCriteriaQuery;

        // 实体条件查询
        private final CriteriaQuery<T> entityCriteriaQuery;

        // FROM 语句中的根类型
        private final Root<T> countRootType;

        // FROM 语句中的跟类型
        private final Root<T> entityRootType;

        // 查询条件中的断言
        private Predicate predicate;

        // 查询排序字段列表
        private final List<Sort.Order> orderList = new ArrayList<>();

        // 页码
        private int pageNo = 0;

        // 页大小
        private int pageSize = 10;

        // 实体查询
        private TypedQuery<T> entitySearchQuery;

        // 符合查询条件的数据总量
        private Long count = null;

        /**
         * 判断是否为空或者为空字符串
         *
         * @param value 值
         * @return 是否为空或者为空字符串
         */
        private static boolean isEmpty(Object value) {
            return value == null
                   || (value instanceof String str && str.isEmpty());
        }

        /**
         * 判断list是否为空
         *
         * @param list 列表
         * @return 是否为空
         */
        private static boolean isListEmpty(List<?> list) {
            return list == null || list.isEmpty();
        }

        /**
         * 压缩列表
         *
         * @param list 列表
         * @return 压缩后的列表
         */
        private static <V> List<V> compressList(List<V> list) {
            if (isListEmpty(list)) {
                return list;
            }

            list.removeIf(SQLQueryBuilder::isEmpty);

            Set<V> set = new HashSet<>(list);

            list.clear();
            list.addAll(set);

            return list;
        }

        /**
         * 去除字符串首尾空格
         *
         * @param value 字符串
         * @return 去除首尾空格后的字符串
         */
        private static Object trim(Object value) {
            return (!(value instanceof String str)) ? value : str.trim();
        }

        public static String escapeLikeValue(String value) {
            if (isEmpty(value)) {
                return value;
            }

            return value
                    .replace("%", "\\\\%")
                    .replace("_", "\\\\_")
                    .replace("\\[", "\\\\[");
        }

        /**
         * 构造方法
         *
         * @param entityManager 实体管理器
         * @param entityClass   实体类
         */
        private SQLQueryBuilder(final EntityManager entityManager, final Class<T> entityClass) {
            this.entityManager = entityManager;

            criteriaBuilder = entityManager.getCriteriaBuilder();
            countCriteriaQuery = criteriaBuilder.createQuery(Long.class);
            countRootType = countCriteriaQuery.from(entityClass);
            entityCriteriaQuery = criteriaBuilder.createQuery(entityClass);
            entityRootType = entityCriteriaQuery.from(entityClass);
            predicate = criteriaBuilder.conjunction();

            countRootType.alias("__tbl");
            entityRootType.alias("__tbl");
        }

        /**
         * 添加 OR 条件到查询中。
         * <p>
         * 该方法接收一个嵌套的 Map，其中外层 Map 的键是字段名，内层 Map 的键值对表示操作符和对应的值。
         * 每个内层 Map 的键值对会被转换为一个条件，并通过 OR 连接起来。
         * 如果传入的条件为空，则直接返回当前构建器实例。
         *
         * @param orCriteria 嵌套的 Map，表示 OR 条件集合
         *                   外层 Map 的键是字段名，内层 Map 的键值对表示操作符和值。
         *                   例如：
         *                   {
         *                   "name": {
         *                   "$like": "%John%"
         *                   },
         *                   "age": {
         *                   "$gt": "30"
         *                   }
         *                   }
         * @return 当前 SQLQueryBuilder 实例（支持链式调用）
         * <p>
         * 示例：
         * SQLQueryBuilder<User> queryBuilder = new SQLQueryBuilder<>(entityManager, User.class);
         * <p>
         * Map<String, Map<String, String>> orConditions = new HashMap<>();
         * <p>
         * orConditions.put("name", Map.of("$like", "%John%"));
         * <p>
         * orConditions.put("age", Map.of("$gt", "30"));
         * <p>
         * queryBuilder.or(orConditions);
         */
        public SQLQueryBuilder<T> or(Map<String, Map<String, String>> orCriteria) {
            if (orCriteria.isEmpty()) {
                return this;
            }

            Predicate orPredicate = criteriaBuilder.disjunction();

            for (Map.Entry<String, Map<String, String>> criterionMap : orCriteria.entrySet()) {
                String key = criterionMap.getKey();

                if (criterionMap.getValue() != null && !criterionMap.getValue().isEmpty()) {
                    for (Map.Entry<String, String> criterion : criterionMap.getValue().entrySet()) {
                        Predicate criterionPredicate = buildCriterionPredicate(key, criterion.getKey(), criterion.getValue());
                        if (criterionPredicate != null) {
                            orPredicate = criteriaBuilder.or(orPredicate, criterionPredicate);
                        }
                    }
                }
            }

            predicate = criteriaBuilder.and(predicate, orPredicate);

            return this;
        }

        /**
         * 构建OR条件查询，支持Object类型的值。
         * <p>
         * 该方法允许传入多个字段的多种条件，这些条件之间使用OR逻辑连接。
         * 每个字段可以有多个操作条件，字段内的条件使用OR连接，不同字段之间也使用OR连接。
         * 支持的操作符包括：$is、$like、$isNotNull、$isNull、$isNot、$gt。
         * <p>
         * 使用示例：
         * <pre>
         * Map&lt;String, Map&lt;String, Object&gt;&gt; criteria = new HashMap&lt;&gt;();
         * Map&lt;String, Object&gt; nameConditions = new HashMap&lt;&gt;();
         * nameConditions.put("$like", "张");
         * nameConditions.put("$isNotNull", true);
         * criteria.put("name", nameConditions);
         *
         * Map&lt;String, Object&gt; ageConditions = new HashMap&lt;&gt;();
         * ageConditions.put("$gt", 18);
         * criteria.put("age", ageConditions);
         *
         * queryBuilder.orObj(criteria); // (name LIKE '%张%' OR name IS NOT NULL) OR (age > 18)
         * </pre>
         *
         * @param orCriteria OR条件映射，键为字段名，值为该字段的条件映射（操作符 -> 值）
         * @return 当前SQLQueryBuilder实例，支持链式调用
         */
        public SQLQueryBuilder<T> orObj(Map<String, Map<String, Object>> orCriteria) {
            if (orCriteria.isEmpty()) {
                return this;
            }

            Predicate orPredicate = criteriaBuilder.disjunction();

            for (Map.Entry<String, Map<String, Object>> criterionMap : orCriteria.entrySet()) {
                String key = criterionMap.getKey();

                if (criterionMap.getValue() != null && !criterionMap.getValue().isEmpty()) {
                    for (Map.Entry<String, Object> criterion : criterionMap.getValue().entrySet()) {
                        Predicate criterionPredicate = buildObjectCriterionPredicate(key, criterion.getKey(), criterion.getValue());
                        if (criterionPredicate != null) {
                            orPredicate = criteriaBuilder.or(orPredicate, criterionPredicate);
                        }
                    }
                }
            }

            predicate = criteriaBuilder.and(predicate, orPredicate);

            return this;
        }

        /**
         * 添加等于条件查询。
         * <p>
         * 该方法是 {@link #is(String, Object, From)} 的简化版本，
         * 使用默认的实体根类型（entityRootType）作为查询源。
         * 用于构建字段等于指定值的查询条件。
         * <p>
         * 使用示例：
         * <pre>
         * queryBuilder.is("name", "张三");     // WHERE name = '张三'
         * queryBuilder.is("age", 25);        // WHERE age = 25
         * queryBuilder.is("status", Status.ACTIVE); // WHERE status = 'ACTIVE'
         * </pre>
         *
         * @param key   实体字段名，支持嵌套属性（如"user.name"）
         * @param value 比较值，支持基本类型和枚举类型
         * @return 当前SQLQueryBuilder实例，支持链式调用
         */
        public SQLQueryBuilder<T> is(String key, Object value) {
            return is(key, value, entityRootType);
        }

        /**
         * 添加等于条件查询，支持指定查询源。
         * <p>
         * 该方法用于构建字段等于指定值的查询条件，支持指定不同的查询源（From对象）。
         * 这在处理关联查询或子查询时特别有用。方法会自动处理嵌套属性路径，
         * 支持通过点号分隔的属性名访问关联对象的属性。
         * <p>
         * 值的有效性检查：
         * <ul>
         * <li>如果值为空（null或空字符串），则跳过该条件</li>
         * <li>如果值不是基本类型且不是枚举类型，则跳过该条件</li>
         * <li>有效的值会被自动trim处理（去除前后空格）</li>
         * </ul>
         * <p>
         * 使用示例：
         * <pre>
         * // 简单字段查询
         * queryBuilder.is("name", "张三", userRoot);
         *
         * // 嵌套属性查询
         * queryBuilder.is("department.name", "技术部", userRoot);
         *
         * // 枚举类型查询
         * queryBuilder.is("status", UserStatus.ACTIVE, userRoot);
         * </pre>
         *
         * @param key   实体字段名，支持嵌套属性（如"user.name"）
         * @param value 比较值，支持基本类型和枚举类型
         * @param from  查询源，指定从哪个实体或关联开始查询
         * @return 当前SQLQueryBuilder实例，支持链式调用
         */
        public SQLQueryBuilder<T> is(String key, Object value, From<?, ?> from) {
            if (isEmpty(value) || (!DataTypeUtil.isPrimitiveType(value) && !(value instanceof Enum<?>))) {
                return this;
            }

            Path<?> finalPath = getPathByKey(key, from);

            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.equal(finalPath, trim(value))
            );

            return this;
        }

        /**
         * 添加不等于条件到查询中
         * <p>
         * 该方法用于构建 SQL 查询中的 NOT EQUAL 条件，只有当值不为空且为基本类型或枚举类型时才会添加条件。
         * 对于空值或非基本类型（除枚举外）的值，该方法会直接返回当前构建器实例而不添加任何条件。
         * </p>
         *
         * <p>使用示例：</p>
         * <pre>
         * // 查询状态不等于 "INACTIVE" 的记录
         * builder.isNot("status", "INACTIVE");
         *
         * // 查询年龄不等于 25 的记录
         * builder.isNot("age", 25);
         *
         * // 查询类型不等于指定枚举值的记录
         * builder.isNot("type", UserType.ADMIN);
         * </pre>
         *
         * @param key   实体属性名，用于指定要比较的字段
         * @param value 比较值，必须是基本类型或枚举类型，空值会被忽略
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> isNot(String key, Object value) {
            if (isEmpty(value) || (!DataTypeUtil.isPrimitiveType(value) && !(value instanceof Enum<?>))) {
                return this;
            }

            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.notEqual(entityRootType.get(key), trim(value))
            );

            return this;
        }

        /**
         * 添加非空条件到查询中
         * <p>
         * 该方法用于构建 SQL 查询中的 IS NOT NULL 条件，用于筛选指定字段值不为空的记录。
         * 这是一个无条件添加的方法，不会进行任何值验证。
         * </p>
         *
         * <p>使用示例：</p>
         * <pre>
         * // 查询邮箱不为空的用户
         * builder.isNotNull("email");
         *
         * // 查询创建时间不为空的记录
         * builder.isNotNull("createTime");
         * </pre>
         *
         * @param key 实体属性名，用于指定要检查非空的字段
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> isNotNull(String key) {
            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.isNotNull(entityRootType.get(key))
            );

            return this;
        }

        /**
         * 添加空值条件到查询中
         * <p>
         * 该方法用于构建 SQL 查询中的 IS NULL 条件，用于筛选指定字段值为空的记录。
         * 这是一个无条件添加的方法，不会进行任何值验证。
         * </p>
         *
         * <p>使用示例：</p>
         * <pre>
         * // 查询删除时间为空（未删除）的记录
         * builder.isNUll("deleteTime");
         *
         * // 查询备注为空的记录
         * builder.isNUll("remark");
         * </pre>
         *
         * @param key 实体属性名，用于指定要检查空值的字段
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> isNUll(String key) {
            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.isNull(entityRootType.get(key))
            );

            return this;
        }

        /**
         * 添加小于条件到查询中
         * <p>
         * 该方法用于构建 SQL 查询中的小于（&lt;）条件，用于筛选指定数值字段小于给定值的记录。
         * 当传入的值为空时，该方法会直接返回当前构建器实例而不添加任何条件。
         * </p>
         *
         * <p>使用示例：</p>
         * <pre>
         * // 查询年龄小于 30 的用户
         * builder.lt("age", 30);
         *
         * // 查询价格小于 100.0 的商品
         * builder.lt("price", 100.0);
         *
         * // 查询创建时间小于指定时间戳的记录
         * builder.lt("createTimestamp", 1640995200000L);
         * </pre>
         *
         * @param key   实体属性名，用于指定要比较的数值字段
         * @param value 比较值，必须是 Number 类型，空值会被忽略
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> lt(String key, Number value) {
            if (isEmpty(value)) {
                return this;
            }

            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.lt(entityRootType.get(key), value)
            );

            return this;
        }

        /**
         * 添加小于等于条件到查询中
         * <p>
         * 该方法用于构建 SQL 查询中的小于等于（&lt;=）条件，用于筛选指定数值字段小于或等于给定值的记录。
         * 当传入的值为空时，该方法会直接返回当前构建器实例而不添加任何条件。
         * </p>
         *
         * <p>使用示例：</p>
         * <pre>
         * // 查询年龄小于等于 65 的用户
         * builder.le("age", 65);
         *
         * // 查询库存小于等于 10 的商品
         * builder.le("stock", 10);
         *
         * // 查询评分小于等于 4.5 的记录
         * builder.le("rating", 4.5);
         * </pre>
         *
         * @param key   实体属性名，用于指定要比较的数值字段
         * @param value 比较值，必须是 Number 类型，空值会被忽略
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> le(String key, Number value) {
            if (isEmpty(value)) {
                return this;
            }

            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.le(entityRootType.get(key), value)
            );

            return this;
        }

        /**
         * 添加大于条件到查询中
         * <p>
         * 该方法用于构建 SQL 查询中的大于（&gt;）条件，用于筛选指定数值字段大于给定值的记录。
         * 当传入的值为空时，该方法会直接返回当前构建器实例而不添加任何条件。
         * </p>
         *
         * <p>使用示例：</p>
         * <pre>
         * // 查询年龄大于 18 的用户
         * builder.gt("age", 18);
         *
         * // 查询价格大于 50.0 的商品
         * builder.gt("price", 50.0);
         *
         * // 查询访问次数大于 100 的记录
         * builder.gt("visitCount", 100);
         * </pre>
         *
         * @param key   实体属性名，用于指定要比较的数值字段
         * @param value 比较值，必须是 Number 类型，空值会被忽略
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> gt(String key, Number value) {
            if (isEmpty(value)) {
                return this;
            }

            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.gt(entityRootType.get(key), value)
            );

            return this;
        }

        /**
         * 添加大于等于条件到查询中
         * <p>
         * 该方法用于构建 SQL 查询中的大于等于（&gt;=）条件，用于筛选指定数值字段大于或等于给定值的记录。
         * 当传入的值为空时，该方法会直接返回当前构建器实例而不添加任何条件。
         * </p>
         *
         * <p>使用示例：</p>
         * <pre>
         * // 查询年龄大于等于 18 的成年用户
         * builder.ge("age", 18);
         *
         * // 查询评分大于等于 4.0 的高质量记录
         * builder.ge("rating", 4.0);
         *
         * // 查询创建时间大于等于指定日期的记录
         * builder.ge("createTime", startDate);
         * </pre>
         *
         * @param key   实体属性名，用于指定要比较的数值字段
         * @param value 比较值，必须是 Number 类型，空值会被忽略
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> ge(String key, Number value) {
            if (isEmpty(value)) {
                return this;
            }

            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.ge(entityRootType.get(key), value)
            );

            return this;
        }

        /**
         * 添加 IN 条件到查询中（数组版本）
         * <p>
         * 该方法用于构建 SQL 查询中的 IN 条件，接受数组类型的值列表。
         * 当传入的数组为空或 null 时，该方法会直接返回当前构建器实例而不添加任何条件。
         * 该方法内部会将数组转换为 List 并调用 {@link #in(String, List)} 方法。
         * </p>
         *
         * <p>使用示例：</p>
         * <pre>
         * // 查询状态为 "ACTIVE" 或 "PENDING" 的记录
         * String[] statuses = {"ACTIVE", "PENDING"};
         * builder.in("status", statuses);
         *
         * // 查询 ID 在指定范围内的记录
         * Integer[] ids = {1, 2, 3, 4, 5};
         * builder.in("id", ids);
         *
         * // 查询类型为指定枚举值的记录
         * UserType[] types = {UserType.ADMIN, UserType.MODERATOR};
         * builder.in("userType", types);
         * </pre>
         *
         * @param key    实体属性名，用于指定要匹配的字段
         * @param values 值数组，空数组或 null 会被忽略
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> in(String key, Object[] values) {
            if (values == null || values.length == 0) {
                return this;
            }

            in(key, new ArrayList<>(Arrays.asList(values)));

            return this;
        }

        /**
         * 添加 IN 条件到查询中（列表版本）
         * <p>
         * 该方法用于构建 SQL 查询中的 IN 条件，接受 List 类型的值列表。
         * 该方法会先对列表进行压缩处理（移除空值），如果压缩后的列表为空，则不添加任何条件。
         * 支持嵌套属性路径的查询，通过 {@link #getPathByKey(String, From)} 方法解析属性路径。
         * </p>
         *
         * <p>使用示例：</p>
         * <pre>
         * // 查询状态在指定列表中的记录
         * List&lt;String&gt; statuses = Arrays.asList("ACTIVE", "PENDING", "COMPLETED");
         * builder.in("status", statuses);
         *
         * // 查询用户 ID 在指定列表中的记录
         * List&lt;Long&gt; userIds = Arrays.asList(1L, 2L, 3L);
         * builder.in("userId", userIds);
         *
         * // 查询嵌套属性的 IN 条件
         * List&lt;String&gt; departmentNames = Arrays.asList("IT", "HR", "Finance");
         * builder.in("department.name", departmentNames);
         * </pre>
         *
         * @param key    实体属性名，支持嵌套属性路径（如 "user.department.name"）
         * @param values 值列表，空列表或只包含空值的列表会被忽略
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> in(String key, List<?> values) {
            if (isListEmpty(compressList(values))) {
                return this;
            }

            Path<?> finalPath = getPathByKey(key, entityRootType);

            predicate = criteriaBuilder.and(
                    predicate,
                    finalPath.in(values)
            );

            return this;
        }

        /**
         * 添加 NOT IN 条件到查询中
         * <p>
         * 该方法用于构建 SQL 查询中的 NOT IN 条件，用于筛选指定字段值不在给定列表中的记录。
         * 该方法会先对列表进行压缩处理（移除空值），如果压缩后的列表为空，则不添加任何条件。
         * 支持嵌套属性路径的查询，通过 {@link #getPathByKey(String, From)} 方法解析属性路径。
         * 内部实现是先构建 IN 条件，然后对其进行逻辑取反操作。
         * </p>
         *
         * <p>使用示例：</p>
         * <pre>
         * // 查询状态不在指定列表中的记录
         * List&lt;String&gt; excludeStatuses = Arrays.asList("DELETED", "ARCHIVED");
         * builder.notIn("status", excludeStatuses);
         *
         * // 查询用户 ID 不在黑名单中的记录
         * List&lt;Long&gt; blacklistIds = Arrays.asList(100L, 200L, 300L);
         * builder.notIn("userId", blacklistIds);
         *
         * // 查询部门不在指定列表中的员工
         * List&lt;String&gt; excludeDepartments = Arrays.asList("TEMP", "INTERN");
         * builder.notIn("department.name", excludeDepartments);
         *
         * // 查询类型不为指定枚举值的记录
         * List&lt;UserType&gt; excludeTypes = Arrays.asList(UserType.GUEST, UserType.BANNED);
         * builder.notIn("userType", excludeTypes);
         * </pre>
         *
         * @param key    实体属性名，支持嵌套属性路径（如 "user.department.name"）
         * @param values 排除值列表，空列表或只包含空值的列表会被忽略
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> notIn(String key, List<?> values) {
            if (isListEmpty(compressList(values))) {
                return this;
            }

            Path<?> finalPath = getPathByKey(key, entityRootType);

            predicate = criteriaBuilder.and(
                    predicate,
                    finalPath.in(values).not()
            );

            return this;
        }

        /**
         * 添加模糊查询条件到查询中
         * <p>
         * 该方法用于构建 SQL 查询中的 LIKE 条件，实现模糊匹配功能。
         * 当传入的值为空时，该方法会直接返回当前构建器实例而不添加任何条件。
         * 该方法会自动在查询值的前后添加通配符 %，并对特殊字符进行转义处理。
         * </p>
         * 
         * <p>使用示例：</p>
         * <pre>
         * // 查询用户名包含 "admin" 的用户
         * builder.like("username", "admin");
         * 
         * // 查询标题包含 "Java" 的文章
         * builder.like("title", "Java");
         * 
         * // 查询描述包含 "Spring Boot" 的项目
         * builder.like("description", "Spring Boot");
         * </pre>
         * 
         * @param key 实体属性名，用于指定要进行模糊匹配的字段
         * @param value 匹配值，空值会被忽略，特殊字符会被自动转义
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> like(String key, String value) {
            if (isEmpty(value)) {
                return this;
            }

            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.like(
                            entityRootType.get(key),
                            "%" + escapeLikeValue((String) trim(value)) + "%",
                            '\\'
                    )
            );

            return this;
        }

        /**
         * 添加时间范围查询条件到查询中
         * <p>
         * 该方法用于构建 SQL 查询中的时间范围条件，支持灵活的时间区间查询。
         * 根据传入的开始时间和结束时间参数，自动选择合适的查询条件：
         * <ul>
         * <li>只有开始时间：使用大于等于条件（&gt;=）</li>
         * <li>只有结束时间：使用小于等于条件（&lt;=）</li>
         * <li>同时有开始和结束时间：使用 BETWEEN 条件</li>
         * <li>两个时间都为空：不添加任何条件</li>
         * </ul>
         * </p>
         * 
         * <p>使用示例：</p>
         * <pre>
         * // 查询指定时间范围内创建的记录
         * Date start = new Date(System.currentTimeMillis() - 86400000); // 昨天
         * Date end = new Date(); // 现在
         * builder.between("createTime", start, end);
         * 
         * // 查询指定日期之后的记录
         * builder.between("updateTime", startDate, null);
         * 
         * // 查询指定日期之前的记录
         * builder.between("expireTime", null, endDate);
         * </pre>
         * 
         * @param key 实体属性名，用于指定要进行时间范围查询的字段
         * @param startTime 开始时间，可以为 null
         * @param endTime 结束时间，可以为 null
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> between(String key, Date startTime, Date endTime) {
            if (isEmpty(key) || (isEmpty(startTime) && isEmpty(endTime))) {
                return this;
            }
            if (!isEmpty(startTime) && isEmpty(endTime)) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(entityRootType.get(key), startTime)
                );
            } else if (isEmpty(startTime) && !isEmpty(endTime)) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(entityRootType.get(key), endTime)
                );
            } else {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.between(entityRootType.get(key), startTime, endTime)
                );
            }

            return this;
        }

        /**
         * 添加分组条件到查询中
         * <p>
         * 该方法用于构建 SQL 查询中的 GROUP BY 子句，按指定字段对查询结果进行分组。
         * 当传入的字段名为空时，该方法会直接返回当前构建器实例而不添加任何分组条件。
         * 通常与聚合函数（如 COUNT、SUM、AVG 等）一起使用。
         * </p>
         * 
         * <p>使用示例：</p>
         * <pre>
         * // 按部门分组统计员工数量
         * builder.groupBy("department");
         * 
         * // 按创建日期分组统计订单
         * builder.groupBy("createDate");
         * 
         * // 按状态分组统计任务
         * builder.groupBy("status");
         * </pre>
         * 
         * @param key 实体属性名，用于指定分组的字段
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> groupBy(String key) {
            if (isEmpty(key)) {
                return this;
            }
            entityCriteriaQuery.groupBy(entityRootType.get(key));
            return this;
        }

        /**
         * 添加升序排序条件到查询中
         * <p>
         * 该方法用于构建 SQL 查询中的 ORDER BY 子句，按指定字段进行升序排序。
         * 当传入的字段名为空时，该方法会直接返回当前构建器实例而不添加任何排序条件。
         * 可以多次调用该方法来添加多个排序字段，排序优先级按调用顺序确定。
         * </p>
         * 
         * <p>使用示例：</p>
         * <pre>
         * // 按创建时间升序排序
         * builder.asc("createTime");
         * 
         * // 按姓名升序排序
         * builder.asc("name");
         * 
         * // 多字段排序：先按部门升序，再按工资升序
         * builder.asc("department").asc("salary");
         * </pre>
         * 
         * @param key 实体属性名，用于指定排序的字段
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> asc(String key) {
            orderList.add(new Sort.Order(Sort.Direction.ASC, key));
            return this;
        }

        /**
         * 添加降序排序条件到查询中
         * <p>
         * 该方法用于构建 SQL 查询中的 ORDER BY 子句，按指定字段进行降序排序。
         * 当传入的字段名为空时，该方法会直接返回当前构建器实例而不添加任何排序条件。
         * 可以多次调用该方法来添加多个排序字段，排序优先级按调用顺序确定。
         * </p>
         * 
         * <p>使用示例：</p>
         * <pre>
         * // 按创建时间降序排序（最新的在前）
         * builder.desc("createTime");
         * 
         * // 按评分降序排序（最高分在前）
         * builder.desc("score");
         * 
         * // 多字段排序：先按优先级降序，再按创建时间降序
         * builder.desc("priority").desc("createTime");
         * </pre>
         * 
         * @param key 实体属性名，用于指定排序的字段
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> desc(String key) {
            orderList.add(new Sort.Order(Sort.Direction.DESC, key));
            return this;
        }

        /**
         * 添加排序条件到查询中（Sort.Order版本）
         * <p>
         * 该方法用于构建 SQL 查询中的 ORDER BY 子句，接受 Spring Data 的 Sort.Order 对象。
         * 根据 Sort.Order 中的排序方向自动调用相应的升序或降序方法。
         * 当传入的 Sort.Order 为 null 或方向不明确时，该方法会直接返回当前构建器实例。
         * </p>
         * 
         * <p>使用示例：</p>
         * <pre>
         * // 使用 Sort.Order 对象进行排序
         * Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createTime");
         * builder.sort(order);
         * 
         * // 结合 Spring Data 的排序功能
         * Sort.Order nameOrder = Sort.Order.asc("name");
         * builder.sort(nameOrder);
         * </pre>
         * 
         * @param order Sort.Order 对象，包含排序字段和方向信息
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> sort(Sort.Order order) {
            if (order.getDirection() == Sort.Direction.ASC) {
                asc(order.getProperty());
            } else if (order.getDirection() == Sort.Direction.DESC) {
                desc(order.getProperty());
            }

            return this;
        }

        /**
         * 添加批量排序条件到查询中
         * <p>
         * 该方法用于构建 SQL 查询中的 ORDER BY 子句，接受 Spring Data 的 Sort 对象。
         * Sort 对象可以包含多个排序条件，该方法会遍历所有排序条件并依次添加到查询中。
         * 当传入的 Sort 为 null 时，该方法会直接返回当前构建器实例而不添加任何排序条件。
         * </p>
         * 
         * <p>使用示例：</p>
         * <pre>
         * // 创建多字段排序
         * Sort sort = Sort.by(
         *     Sort.Order.desc("priority"),
         *     Sort.Order.asc("createTime")
         * );
         * builder.sort(sort);
         * 
         * // 结合 Pageable 的排序功能
         * Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
         * builder.sort(pageable.getSort());
         * </pre>
         * 
         * @param sort Sort 对象，包含多个排序条件
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> sort(Sort sort) {
            if (sort == null) {
                return this;
            }
            for (Sort.Order order : sort) {
                sort(order);
            }

            return this;
        }

        /**
         * 设置查询的页码
         * <p>
         * 该方法用于设置分页查询中的页码（从0开始）。
         * 如果传入的页码小于0，会自动调整为0。
         * 页码与页大小配合使用来实现分页查询功能。
         * </p>
         * 
         * <p>使用示例：</p>
         * <pre>
         * // 查询第一页（页码从0开始）
         * builder.page(0).limit(10);
         * 
         * // 查询第三页
         * builder.page(2).limit(20);
         * 
         * // 结合其他条件进行分页查询
         * builder.like("name", "张").page(1).limit(15);
         * </pre>
         * 
         * @param pageNo 页码，从0开始，负数会被调整为0
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> page(int pageNo) {
            this.pageNo = Math.max(0, pageNo);
            return this;
        }

        /**
         * 设置查询的页大小
         * <p>
         * 该方法用于设置分页查询中每页返回的记录数量。
         * 如果传入的页大小小于1，会自动调整为1。
         * 页大小与页码配合使用来实现分页查询功能。
         * </p>
         * 
         * <p>使用示例：</p>
         * <pre>
         * // 设置每页显示10条记录
         * builder.limit(10);
         * 
         * // 设置每页显示50条记录
         * builder.limit(50);
         * 
         * // 结合页码进行分页查询
         * builder.page(0).limit(20); // 第一页，每页20条
         * </pre>
         * 
         * @param pageSize 页大小，必须大于0，小于1的值会被调整为1
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> limit(int pageSize) {
            this.pageSize = Math.max(1, pageSize);
            return this;
        }

        /**
         * 使用 Pageable 对象设置分页和排序条件
         * <p>
         * 该方法用于一次性设置分页查询的所有相关参数，包括页码、页大小和排序条件。
         * 接受 Spring Data 的 Pageable 对象，自动提取其中的分页和排序信息。
         * 当传入的 Pageable 为 null 时，会使用默认的分页设置（第0页，最大页大小）。
         * </p>
         * 
         * <p>使用示例：</p>
         * <pre>
         * // 使用 PageRequest 创建分页对象
         * Pageable pageable = PageRequest.of(0, 10, Sort.by("createTime").descending());
         * builder.paginate(pageable);
         * 
         * // 在 Controller 中直接使用
         * public Page&lt;User&gt; getUsers(Pageable pageable) {
         *     return queryBuilder.like("name", "张").paginate(pageable).execute().page();
         * }
         * 
         * // 创建复杂的分页排序
         * Pageable complexPageable = PageRequest.of(1, 20, 
         *     Sort.by("department").ascending().and(Sort.by("salary").descending()));
         * builder.paginate(complexPageable);
         * </pre>
         * 
         * @param pageable 分页对象，包含页码、页大小和排序信息，null 时使用默认设置
         * @return 当前 SQLQueryBuilder 实例，支持链式调用
         */
        public SQLQueryBuilder<T> paginate(Pageable pageable) {
            if (pageable == null) {
                pageable = PageRequest.of(0, Integer.MAX_VALUE);
            }

            return this
                    .sort(pageable.getSort())
                    .page(pageable.getPageNumber())
                    .limit(pageable.getPageSize());
        }

        /**
         * 执行查询并返回查询结果对象
         * <p>
         * 该方法是查询构建器的核心执行方法，将之前设置的所有查询条件、排序条件和分页参数
         * 应用到 JPA Criteria 查询中，并创建可执行的 TypedQuery 对象。
         * 返回的 SQLQueryResult 对象提供了多种获取查询结果的方式。
         * </p>
         * 
         * <p>执行流程：</p>
         * <ol>
         * <li>将构建的谓词条件应用到查询中</li>
         * <li>如果有排序条件，应用排序规则</li>
         * <li>创建 TypedQuery 对象</li>
         * <li>如果设置了分页参数，应用分页限制</li>
         * <li>返回包装的查询结果对象</li>
         * </ol>
         * 
         * <p>使用示例：</p>
         * <pre>
         * // 执行查询并获取结果列表
         * List&lt;User&gt; users = builder
         *     .like("name", "张")
         *     .desc("createTime")
         *     .page(0).limit(10)
         *     .execute()
         *     .resultList();
         * 
         * // 执行查询并获取分页结果
         * Page&lt;User&gt; userPage = builder
         *     .is("status", "ACTIVE")
         *     .paginate(pageable)
         *     .execute()
         *     .page();
         * 
         * // 执行查询并获取总数
         * long count = builder
         *     .like("email", "@example.com")
         *     .execute()
         *     .count();
         * </pre>
         * 
         * @return SQLQueryResult 查询结果对象，提供多种获取结果的方法
         */
        public SQLQueryBuilder<T> execute() {
            TypedQuery<Long> entityCountQuery = entityManager.createQuery(
                    countCriteriaQuery
                            .select(criteriaBuilder.count(countRootType))
                            .where(predicate)
            );

            if ((count = entityCountQuery.getSingleResult()) == 0) {
                return this;
            }

            entityCriteriaQuery.where(predicate);

            if (!orderList.isEmpty()) {
                List<Order> orderList1 = new ArrayList<>();

                for (Sort.Order order : orderList) {
                    if (order.getDirection() == Sort.Direction.ASC) {
                        orderList1.add(
                                criteriaBuilder.asc(
                                        entityRootType.get(order.getProperty())
                                )
                        );
                    } else if (order.getDirection() == Sort.Direction.DESC) {
                        orderList1.add(
                                criteriaBuilder.desc(
                                        entityRootType.get(order.getProperty())
                                )
                        );
                    }
                }

                entityCriteriaQuery.orderBy(orderList1);
            } else {
                entityCriteriaQuery.orderBy(
                        criteriaBuilder.desc(entityRootType.get("id"))
                );
            }

            entitySearchQuery = entityManager
                    .createQuery(entityCriteriaQuery)
                    .setFirstResult(pageNo * pageSize)
                    .setMaxResults(pageSize);

            return this;
        }

        /**
         * 获取符合查询条件的记录总数
         * <p>
         * 该方法返回执行查询后统计的符合条件的记录总数。
         * 必须在调用 {@link #execute()} 方法之后才能获取到正确的计数结果。
         * 如果查询尚未执行，返回的可能是 null 或 0。
         * </p>
         * 
         * <p>使用示例：</p>
         * <pre>
         * // 获取查询结果的总数
         * long totalCount = builder
         *     .like("name", "张")
         *     .execute()
         *     .count();
         * 
         * // 在分页查询中获取总数
         * SQLQueryBuilder&lt;User&gt; queryBuilder = builder
         *     .is("status", "ACTIVE")
         *     .page(0).limit(10)
         *     .execute();
         * 
         * long total = queryBuilder.count();
         * List&lt;User&gt; users = queryBuilder.resultList();
         * </pre>
         * 
         * @return 符合查询条件的记录总数，如果查询未执行则可能返回 null
         */
        public Long count() {
            return count;
        }

        /**
         * 获取查询结果列表
         * <p>
         * 该方法返回执行查询后的实体对象列表。
         * 必须在调用 {@link #execute()} 方法之后才能获取到查询结果。
         * 如果查询尚未执行或没有符合条件的记录，返回空列表。
         * 如果设置了分页参数，返回的列表将只包含当前页的记录。
         * </p>
         * 
         * <p>使用示例：</p>
         * <pre>
         * // 获取查询结果列表
         * List&lt;User&gt; users = builder
         *     .like("name", "张")
         *     .desc("createTime")
         *     .execute()
         *     .resultList();
         * 
         * // 获取分页结果列表
         * List&lt;User&gt; pageUsers = builder
         *     .is("status", "ACTIVE")
         *     .page(0).limit(10)
         *     .execute()
         *     .resultList();
         * </pre>
         * 
         * @return 符合查询条件的实体对象列表，如果查询未执行或无结果则返回空列表
         */
        public List<T> resultList() {
            return entitySearchQuery == null ? new ArrayList<>() : entitySearchQuery.getResultList();
        }

        /**
         * 获取分页查询结果
         * <p>
         * 该方法返回 Spring Data 的 Page 对象，包含当前页的数据、总记录数、总页数等分页信息。
         * 必须在调用 {@link #execute()} 方法之后才能获取到正确的分页结果。
         * 如果查询尚未执行，返回 null。
         * </p>
         * 
         * <p>使用示例：</p>
         * <pre>
         * // 获取分页查询结果
         * Page&lt;User&gt; userPage = builder
         *     .like("name", "张")
         *     .desc("createTime")
         *     .page(0).limit(10)
         *     .execute()
         *     .page();
         * 
         * // 使用 Pageable 对象
         * Pageable pageable = PageRequest.of(1, 20);
         * Page&lt;Product&gt; productPage = builder
         *     .is("category", "电子产品")
         *     .paginate(pageable)
         *     .execute()
         *     .page();
         * 
         * // 获取分页信息
         * long totalElements = userPage.getTotalElements();
         * int totalPages = userPage.getTotalPages();
         * List&lt;User&gt; content = userPage.getContent();
         * </pre>
         * 
         * @return Spring Data 的 Page 对象，包含分页数据和元信息，如果查询未执行则返回 null
         */
        public Page<T> page() {
            if (count == null) {
                return null;
            }

            return new PageImpl<>(
                    resultList(),
                    PageRequest.of(pageNo, pageSize, Sort.by(orderList)),
                    count()
            );
        }

        private Path<?> getPathByKey(String key, From<?, ?> entityRootType) {
            Path<?> finalPath;
            if (!key.contains(".")) {
                finalPath = entityRootType.get(key);
            } else {
                String[] pathStr = key.split("\\.");
                Path<?> tempPath = entityRootType.get(pathStr[0]);
                for (int i = 1; i < pathStr.length; i++) {
                    tempPath = tempPath.get(pathStr[i]);
                }
                finalPath = tempPath;
            }

            return finalPath;
        }

        private Predicate buildObjectCriterionPredicate(String key, String operation, Object value) {
            return switch (operation) {
                case "$is" -> {
                    if (isEmpty(value) || (DataTypeUtil.isPrimitiveType(value) && !(value instanceof Enum<?>))) {
                        yield null;
                    }
                    yield criteriaBuilder.equal(entityRootType.get(key), trim(value));
                }
                case "$like" -> {
                    if (isEmpty(value)) {
                        yield null;
                    }
                    yield criteriaBuilder.like(
                            entityRootType.get(key),
                            "%" + escapeLikeValue((String) trim(value)) + "%",
                            '\\'
                    );
                }
                case "$isNotNull" -> criteriaBuilder.isNotNull(entityRootType.get(key));
                case "$isNull" -> criteriaBuilder.isNull(entityRootType.get(key));
                case "$isNot" -> criteriaBuilder.notEqual(entityRootType.get(key), value);
                case "$gt" -> {
                    if (isEmpty(value)) {
                        yield null;
                    }
                    yield criteriaBuilder.gt(entityRootType.get(key), Double.parseDouble(value.toString()));
                }
                default -> null;
            };
        }

        private Predicate buildCriterionPredicate(String key, String operation, String value) {
            return switch (operation) {
                case "$is" -> {
                    if (!DataTypeUtil.isPrimitiveType(value) || isEmpty(value)) {
                        yield null;
                    }
                    yield criteriaBuilder.equal(entityRootType.get(key), trim(value));
                }
                case "$like" -> {
                    if (isEmpty(value)) {
                        yield null;
                    }
                    yield criteriaBuilder.like(
                            entityRootType.get(key),
                            "%" + escapeLikeValue((String) trim(value)) + "%",
                            '\\'
                    );
                }
                case "$isNotNull" -> criteriaBuilder.isNotNull(entityRootType.get(key));
                case "$isNot" -> criteriaBuilder.notEqual(entityRootType.get(key), trim(value));
                case "$gt" -> {
                    if (isEmpty(value)) {
                        yield null;
                    }
                    yield criteriaBuilder.gt(entityRootType.get(key), Double.parseDouble(value));
                }
                default -> null;
            };
        }
    }
}

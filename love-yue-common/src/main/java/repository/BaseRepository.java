package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Sort;
import uitls.DataTypeUtil;

import java.util.*;

/**
 * @Description: 数据仓库抽象类
 * @Date 2025/6/19
 * @Author LoveYue
 */
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
         * 根据字段名、操作符和值构建单个条件谓词。
         * <p>
         * 该方法支持多种操作符，包括：
         * - "$is"：等于操作，判断字段是否等于给定值。
         * - "$like"：模糊匹配操作，判断字段是否包含给定值。
         * - "$isNotNull"：非空判断，判断字段是否不为 null。
         * - "$isNot"：不等于操作，判断字段是否不等于给定值。
         * - "$gt"：大于操作，判断字段是否大于给定值。
         * <p>
         * 如果值为空或不符合条件，则返回 null。
         *
         * @param key       字段名
         * @param operation 操作符，例如 "$is", "$like", "$isNotNull" 等
         * @param value     操作符对应的值
         * @return 构建的 Predicate 对象，如果条件无效则返回 null
         * <p>
         * 示例：
         * Predicate = buildCriterionPredicate("name", "$like", "%John%");
         * // 生成的谓词等价于：name LIKE '%John%'
         */
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

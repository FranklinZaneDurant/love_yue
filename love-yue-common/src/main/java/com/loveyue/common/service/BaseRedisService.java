package com.loveyue.common.service;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import com.loveyue.common.uitls.LongUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description: Redis操作
 * @Date 2025/6/25
 * @Author LoveYue
 */
@Component
public abstract class BaseRedisService {
    private static final Logger logger = LoggerFactory.getLogger(BaseRedisService.class);

    @Resource
    private final StringRedisTemplate stringRedisTemplate;

    protected BaseRedisService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    protected void persist(String key) {
        stringRedisTemplate.persist(key);
    }

    /**
     * 设置缓存的值。
     *
     * @param key   key
     * @param value 值
     */
    protected void setRedisKey(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存的值。
     *
     * @param key     key
     * @param value   值
     * @param seconds TTL 秒
     */
    protected void setRedisKey(String key, String value, int seconds) {
        stringRedisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 取得缓存的值。
     *
     * @param key KEY
     * @return 值
     */
    protected String getRedisKey(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 取得缓存的值。
     *
     * @param key     key
     * @param seconds 延长 TTL 秒
     * @return 值
     */
    protected String getRedisKey(String key, int seconds) {

        String value = getRedisKey(key);

        if (value != null) {
            stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        }

        return value;
    }

    /**
     * 删除缓存的值。
     *
     * @param key KEY
     */
    protected void deleteRedisKey(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 判断是否存在缓存的值。
     *
     * @param key KEY
     * @return 是否存在
     */
    protected Boolean hasRedisKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }


    /**
     * 设置Hash的属性
     *
     * @param key   key
     * @param field field
     * @param value value
     * @return return
     */
    public boolean hashSet(String key, String field, String value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
        return true;
    }

    /**
     * 设置Hash的属性
     *
     * @param key   key
     * @param field filed
     * @param value value
     * @return return
     */
    public boolean hashSet(String key, String field, String value, int seconds) {
        stringRedisTemplate.opsForHash().put(key, field, value);
        stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        return true;
    }

    /**
     * 批量设置Hash的属性
     *
     * @param key    key
     * @param fields fields
     * @param values values
     * @return return
     */
    public boolean batchHashSet(String key, String[] fields, String[] values) {
        Map<String, String> hash = new HashMap<>();
        for (int i = 0; i < fields.length; i++) {
            hash.put(fields[i], values[i]);
        }
        stringRedisTemplate.opsForHash().putAll(key, hash);
        return true;
    }

    /**
     * 批量设置Hash的属性
     *
     * @param key key
     * @param map Map
     * @return Hash的属性
     */
    public boolean batchHashSet(String key, Map<String, String> map) {
        stringRedisTemplate.opsForHash().putAll(key, map);

        return true;
    }

    /**
     * 仅当field不存在时设置值，成功返回true
     *
     * @param key   key
     * @param field field
     * @param value value
     */
    public boolean hashSetNull(String key, String field, String value) {
        return stringRedisTemplate.opsForHash().putIfAbsent(key, field, value);

    }

    /**
     * 获取属性的值
     *
     * @param key   key
     * @param field field
     * @return 属性的值
     */
    public String hashGet(String key, String field) {

        return (String) stringRedisTemplate.opsForHash().get(key, field);

    }

    /**
     * 获取属性的值
     *
     * @param key   key
     * @param field field
     * @return 属性的值
     */

    public Long hashGetLong(String key, String field) {

        return LongUtils.parseLong((String) stringRedisTemplate.opsForHash().get(key, field));

    }

    /**
     * 批量获取属性的值
     *
     * @param key    key
     * @param fields fields
     * @return 属性的值
     */
    public List<Object> batchHashGet(String key, String... fields) {

        return stringRedisTemplate.opsForHash().multiGet(key, Collections.singletonList(fields));
    }

    /**
     * 获取在哈希表中指定 key 的所有字段和值
     *
     * @param key key
     * @return Map<String, String>
     */
    public Map<Object, Object> hashGetAll(String key) {

        return stringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除hash的属性
     *
     * @param key    key
     * @param fields fields
     * @return hash的属性
     */
    public boolean hashDelete(String key, String... fields) {

        stringRedisTemplate.opsForHash().delete(key, (Object) fields);
        return true;
    }

    /**
     * 查看哈希表 key 中，指定的字段是否存在。
     *
     * @param key   key
     * @param field field
     */
    public boolean hashExist(String key, String field) {

        return stringRedisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment 。
     *
     * @param key       key
     * @param field     field
     * @param increment 正负数、0、正整数
     */
    public long hashIncrementBy(String key, String field, long increment) {
        return stringRedisTemplate.opsForHash().increment(key, field, increment);
    }

    /**
     * 为哈希表 key 中的指定字段的浮点数值加上增量 increment 。(注：如果field不存在时，会设置新的值)
     *
     * @param key                  key
     * @param field                field
     * @param increment，可以为负数、正数、0
     */
    public Double hashIncrementFloat(String key, String field, double increment) {
        return stringRedisTemplate.opsForHash().increment(key, field, increment);
    }

    /**
     * 获取所有哈希表中的字段
     *
     * @param key key
     * @return Set<String>
     */
    public Set<Object> hashKeySet(String key) {
        return stringRedisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取哈希表中所有值
     *
     * @param key key
     * @return List<String>
     */
    public List<Object> hashAllList(String key) {
        return stringRedisTemplate.opsForHash().values(key);
    }

    /**
     * 获取哈希表中字段的数量，当key不存在时，返回0
     *
     * @param key key
     */
    public Long hashSize(String key) {
        return stringRedisTemplate.opsForHash().size(key);
    }

    /**
     * 迭代哈希表中的键值对。
     *
     * @param key    key
     * @param cursor cursor
     * @return ScanResult<Entry < String, String>>
     */
    public Cursor<Map.Entry<Object, Object>> hasScan(String key, ScanOptions cursor) {
        return stringRedisTemplate.opsForHash().scan(key, cursor);
    }


    /**
     * 向集合添加一个或多个成员，返回添加成功的数量
     *
     * @param key     key
     * @param members members
     * @return Long
     */
    public Long listAdds(String key, String... members) {
        return stringRedisTemplate.opsForSet().add(key, members);
    }

    /**
     * 向集合添加一个或多个成员，返回添加成功的数量
     *
     * @param key    key
     * @param member member
     * @return Long
     */
    public Long listAdd(String key, String member) {
        return stringRedisTemplate.opsForSet().add(key, member);
    }

    /**
     * 获取集合的成员数
     *
     * @param key key
     */
    public Long listCard(String key) {
        return stringRedisTemplate.opsForSet().size(key);
    }

    /**
     * 返回集合中的所有成员
     *
     * @param key key
     * @return Set<String>
     */
    public Set<String> listMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    /**
     * 判断 member 元素是否是集合 key 的成员，在集合中返回True
     *
     * @param key    key
     * @param member member
     * @return Boolean
     */
    public Boolean listHasMember(String key, String member) {
        return stringRedisTemplate.opsForSet().isMember(key, member);
    }

    /**
     * 返回给定所有集合的差集（获取第一个key中与其它key不相同的值，当只有一个key时，就返回这个key的所有值）
     *
     * @param key1 key2
     * @return Set<String>
     */
    public Set<String> listDifference(String key1, String key2) {
        return stringRedisTemplate.opsForSet().difference(key1, key2);
    }

    /**
     * 返回给定所有集合的差集并存储在 targetKey中，类似listDifference，只是该方法把返回的差集保存到targetKey中
     * 当有差集时，返回true
     * 当没有差集时，返回false
     *
     * @param targetKey targetKey
     * @param key1      key1
     */
    public Long listDifferenceStore(String targetKey, String key1) {
        return stringRedisTemplate.opsForSet().differenceAndStore(targetKey, key1, targetKey);
    }

    /**
     * 返回给定所有集合的交集（获取第一个key中与其它key相同的值，要求所有key都要有相同的值，如果没有相同，返回Null。当只有一个key时，就返回这个key的所有值）
     *
     * @param key1 key1
     * @return Set<String>
     */
    public Set<String> listIntersect(String key1, String key2) {

        return stringRedisTemplate.opsForSet().intersect(key1, key2);
    }

    /**
     * 返回给定所有集合的交集并存储在 targetKey中，类似listIntersect
     *
     * @param targetKey targetKey
     * @param key1      key1
     * @return boolean
     */
    public Long listIntersectStore(String targetKey, String key1) {

        return stringRedisTemplate.opsForSet().intersectAndStore(targetKey, key1, targetKey);
    }

    /**
     * 将 member 元素从 sourceKey 集合移动到 targetKey 集合
     * 成功返回true
     * 当member不存在于sourceKey时，返回false
     * 当sourceKey不存在时，也返回false
     *
     * @param sourceKey sourceKey
     * @param targetKey targetKey
     * @param member    member
     * @return boolean
     */
    public boolean listMove(String sourceKey, String targetKey, String member) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().move(sourceKey, targetKey, member));

    }

    /**
     * 移除并返回集合中的一个随机元素
     * 当set为空或者不存在时，返回Null
     *
     * @param key key
     * @return String
     */
    public String listPop(String key) {

        return stringRedisTemplate.opsForSet().pop(key);
    }

    /**
     * 返回集合中一个或多个随机数
     * 当count大于set的长度时，set所有值返回，不会抛错。
     * 当count等于0时，返回[]
     * 当count小于0时，也能返回。如-1返回一个，-2返回两个
     *
     * @param key   key
     * @param count count
     * @return List<String>
     */
    public List<String> listRandMember(String key, int count) {

        return stringRedisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 移除集合中一个或多个成员
     *
     * @param key     key
     * @param members members
     */
    public boolean listRemove(String key, String... members) {
        Long value = stringRedisTemplate.opsForSet().remove(key, (Object) members);

        return value != null && value > 0;
    }

    /**
     * 返回所有给定集合的并集，相同的只会返回一个
     *
     * @param key1 key1
     */
    public Set<String> listUnion(String key1, String key2) {

        return stringRedisTemplate.opsForSet().union(key1, key2);
    }

    /**
     * 所有给定集合的并集存储在targetKey集合中
     * 注：合并时，只会把keys中的集合返回，不包括targetKey本身
     * 如果targetKey本身是有值的，合并后原来的值是没有的，因为把keys的集合重新赋值给targetKey
     * 要想保留targetKey本身的值，keys要包含原来的targetKey
     *
     * @param targetKey targetKey
     * @param key1      key1
     */
    public boolean listUnionStore(String targetKey, String key1) {
        //返回合并后的长度
        Long statusCode = stringRedisTemplate.opsForSet().unionAndStore(targetKey, key1, targetKey);

        return statusCode != null && statusCode > 0;
    }

    /**
     * 存值
     *
     * @param key   key 键
     * @param value 值
     */
    public boolean listPush(String key, String value) {
        try {
            stringRedisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
            logger.error("存值失败", e);
            return false;
        }
    }

    /**
     * 取值 - 非阻塞式
     *
     * @param key 键
     */
    public String listPopNoBlock(String key) {
        try {
            return stringRedisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            logger.error("取值 - 非阻塞式失败", e);
            return null;
        }
    }

    /**
     * 取值 - <listPopBlock：阻塞式> - 推荐使用
     *
     * @param key      key 键
     * @param timeout  超时时间
     * @param timeUnit 给定单元粒度的时间段
     *                 TimeUnit.DAYS //天
     *                 TimeUnit.HOURS //小时
     *                 TimeUnit.MINUTES //分钟
     *                 TimeUnit.SECONDS //秒
     *                 TimeUnit.MILLISECONDS //毫秒
     */
    public String listPopBlock(String key, long timeout, TimeUnit timeUnit) {
        try {
            return stringRedisTemplate.opsForList().rightPop(key, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("取值 - 阻塞式失败", e);
            return null;
        }
    }

    /**
     * 查看值
     *
     * @param key   key 键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     */
    public List<String> listRange(String key, long start, long end) {
        try {
            return stringRedisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            logger.error("查看值", e);
        }

        return Collections.emptyList();
    }


    public byte[] serialize(Object object) {

        ObjectOutputStream oos;


        ByteArrayOutputStream stream;

        try {
            stream = new ByteArrayOutputStream();

            oos = new ObjectOutputStream(stream);

            oos.writeObject(object);

            return stream.toByteArray();

        } catch (Exception e) {
            logger.error("序列化失败", e);
        }

        return new byte[0];
    }

    public static Object unserialize(byte[] bytes) {

        ByteArrayInputStream stream;

        try {
            stream = new ByteArrayInputStream(bytes);

            ObjectInputStream ois = new ObjectInputStream(stream);

            return ois.readObject();

        } catch (Exception e) {
            logger.error("反序列化失败", e);
        }

        return null;
    }
}

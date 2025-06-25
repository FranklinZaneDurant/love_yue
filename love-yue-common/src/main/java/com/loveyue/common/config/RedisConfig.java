package com.loveyue.common.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import com.loveyue.common.uitls.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;

/**
 * @Description: Redis配置类
 * @Date 2025/6/25
 * @Author LoveYue
 */
@Configuration
@PropertySource("classpath:application.properties")
public class RedisConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 4907594206193718794L;

    @Value("${spring.redis.host:}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.redis.password:}")
    private String password;

    @Value("${spring.redis.database:0}")
    private int database;

    @Value("${spring.redis.timeout:60000}")
    private long timeout;

    @Value("${spring.redis.jedis.pool.min-idle:50}")
    private int poolMinIdle;

    @Value("${spring.redis.jedis.pool.max-idle:300}")
    private int poolMaxIdle;

    @Value("${spring.redis.jedis.pool.max-active:600}")
    private int poolMaxActive;

    @Value("${spring.redis.jedis.pool.max-wait:-1}")
    private long poolMaxWait;

    private transient RedisConnectionFactory redisConnectionFactory = null;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public JedisClientConfiguration getJedisClientConfiguration() {
        GenericObjectPoolConfig<Object> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMinIdle(poolMinIdle);
        poolConfig.setMaxIdle(poolMaxIdle);
        poolConfig.setMaxTotal(poolMaxActive);
        poolConfig.setMaxWait(Duration.ofMillis(poolMaxWait));

        return ((JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder())
                .poolConfig(poolConfig)
                .and()
                .connectTimeout(Duration.ofMillis(timeout))
                .build();
    }

    @Bean
    public RedisConnectionFactory connectionFactory() {
        if (redisConnectionFactory != null) {
            return redisConnectionFactory;
        }

        if (!StringUtils.isEmpty(host)) {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
            config.setPassword(RedisPassword.of(password));
            config.setDatabase(database);

            redisConnectionFactory = new JedisConnectionFactory(
                    config,
                    getJedisClientConfiguration()
            );
        } else {
            redisConnectionFactory = new JedisConnectionFactory();
        }

        return redisConnectionFactory;
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisConnectionFactory factory = connectionFactory();

        if (factory == null) {
            return null;
        }

        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        return redisTemplate;
    }

    @Bean
    public RedisCacheManager cacheManager() {
        RedisConnectionFactory factory = connectionFactory();

        if (factory == null) {
            return null;
        }

        return RedisCacheManager.create(factory);
    }
}

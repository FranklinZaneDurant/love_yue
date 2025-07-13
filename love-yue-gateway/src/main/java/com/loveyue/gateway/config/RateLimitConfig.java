package com.loveyue.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 限流配置类
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Configuration
public class RateLimitConfig {

    /**
     * 基于IP的限流Key解析器
     *
     * @return KeyResolver
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String clientIp = getClientIp(exchange);
            return Mono.just(clientIp);
        };
    }

    /**
     * 基于用户的限流Key解析器
     *
     * @return KeyResolver
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            return Mono.just(userId != null ? userId : "anonymous");
        };
    }

    /**
     * 基于API路径的限流Key解析器
     *
     * @return KeyResolver
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> {
            String path = exchange.getRequest().getURI().getPath();
            return Mono.just(path);
        };
    }

    /**
     * 获取客户端真实IP地址
     *
     * @param exchange ServerWebExchange
     * @return 客户端IP
     */
    private String getClientIp(org.springframework.web.server.ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return Objects.requireNonNull(exchange.getRequest().getRemoteAddress())
                .getAddress().getHostAddress();
    }

}
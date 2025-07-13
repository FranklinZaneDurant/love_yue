package com.loveyue.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 请求日志过滤器
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Slf4j
@Component
public class RequestLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        
        long startTime = System.currentTimeMillis();
        
        String method = request.getMethod().name();
        String path = request.getURI().getPath();
        String query = request.getURI().getQuery();
        String clientIp = getClientIp(request);
        String userAgent = request.getHeaders().getFirst("User-Agent");
        
        log.info("Request started: {} {} from {} - Query: {}, User-Agent: {}",
                method, path, clientIp, query, userAgent);
        
        return chain.filter(exchange).then(
            Mono.fromRunnable(() -> {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                int statusCode = response.getStatusCode() != null ? 
                    response.getStatusCode().value() : 0;
                
                log.info("请求完成：{}{}-状态：{}，持续时间：{}ms",
                        method, path, statusCode, duration);
                
                // 如果响应时间过长，记录警告
                if (duration > 5000) {
                    log.warn("检测到慢请求：{}{}耗时{}ms", method, path, duration);
                }
            })
        );
    }

    /**
     * 获取客户端真实IP地址
     *
     * @param request HTTP请求
     * @return 客户端IP
     */
    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return Objects.requireNonNull(request.getRemoteAddress())
                .getAddress().getHostAddress();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
package com.loveyue.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.loveyue.gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 全局认证过滤器
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("${gateway.whitelist}")
    private List<String> whitelist;

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USERNAME_HEADER = "X-Username";
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        
        log.debug("处理请求： {} {}", request.getMethod(), path);
        
        if (isWhitelisted(path)) {
            log.debug("请求路径{}被列入白名单，跳过身份验证", path);
            return chain.filter(exchange);
        }
        
        // 获取token
        String token = getTokenFromRequest(request);
        if (StrUtil.isBlank(token)) {
            log.warn("在对{}的请求中没有找到令牌", path);
            return unauthorizedResponse(response, "缺少身份验证令牌");
        }
        
        // 验证token
        if (!jwtUtil.validateToken(token)) {
            log.warn("请求{}的令牌无效", path);
            return unauthorizedResponse(response, "无效的认证令牌n");
        }
        
        // 检查token是否在黑名单中
        if (isTokenBlacklisted(token)) {
            log.warn("用于请求{}的黑名单令牌", path);
            return unauthorizedResponse(response, "令牌已被撤销");
        }
        
        // 提取用户信息
        String userId = jwtUtil.getUserIdFromToken(token).orElse(null);
        String username = jwtUtil.getUsernameFromToken(token).orElse(null);
        
        if (StrUtil.isBlank(userId)) {
            log.warn("在请求{}的令牌中找不到用户ID", path);
            return unauthorizedResponse(response, "无效令牌有效载荷");
        }
        
        // 添加用户信息到请求头
        ServerHttpRequest modifiedRequest = request.mutate()
                .header(USER_ID_HEADER, userId)
                .header(USERNAME_HEADER, username)
                .build();
        
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(modifiedRequest)
                .build();
        
        log.debug("用户{}访问{}的身份验证成功", username, path);
        
        return chain.filter(modifiedExchange);
    }

    /**
     * 检查路径是否在白名单中
     *
     * @param path 请求路径
     * @return 是否在白名单中
     */
    private boolean isWhitelisted(String path) {
        if (whitelist == null || whitelist.isEmpty()) {
            return false;
        }
        
        return whitelist.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * 从请求中获取token
     *
     * @param request HTTP请求
     * @return token
     */
    private String getTokenFromRequest(ServerHttpRequest request) {
        // 从Authorization header获取
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StrUtil.isNotBlank(authHeader) && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        
        // 从查询参数获取
        String tokenParam = request.getQueryParams().getFirst("token");
        if (StrUtil.isNotBlank(tokenParam)) {
            return tokenParam;
        }
        
        return null;
    }

    /**
     * 检查token是否在黑名单中
     *
     * @param token JWT token
     * @return 是否在黑名单中
     */
    private boolean isTokenBlacklisted(String token) {
        try {
            String key = TOKEN_BLACKLIST_PREFIX + token;
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("检查令牌黑名单时出错：{}", e.getMessage());
            return true;
        }
    }

    /**
     * 返回未授权响应
     *
     * @param response HTTP响应
     * @param message 错误消息
     * @return Mono<Void>
     */
    private Mono<Void> unauthorizedResponse(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        
        String body = String.format(
                "{\"code\":401,\"message\":\"%s\",\"timestamp\":\"%s\"}",
                message,
                System.currentTimeMillis()
        );
        
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes()))
        );
    }

    @Override
    public int getOrder() {
        return -100;
    }

}
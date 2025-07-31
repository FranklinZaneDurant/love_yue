package com.loveyue.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.loveyue.gateway.client.AuthServiceClient;
import com.loveyue.gateway.dto.TokenValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
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

    private final AuthServiceClient authServiceClient;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("${gateway.whitelist}")
    private List<String> whitelist;

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USERNAME_HEADER = "X-Username";
    private static final String DEPT_ID_HEADER = "X-Dept-Id";
    private static final String ROLES_HEADER = "X-Roles";
    private static final String PERMISSIONS_HEADER = "X-Permissions";
    private static final String DEVICE_ID_HEADER = "X-Device-Id";
    private static final String SESSION_ID_HEADER = "X-Session-Id";

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
        
        // 调用认证服务验证token
        return authServiceClient.validateToken(token)
                .flatMap(validationResponse -> {
                    if (!validationResponse.isValid()) {
                        log.warn("请求{}的令牌验证失败: {}", path, validationResponse.getErrorMessage());
                        return unauthorizedResponse(response, validationResponse.getErrorMessage());
                    }
                    
                    // 添加用户信息到请求头
                    ServerHttpRequest.Builder requestBuilder = request.mutate()
                            .header(USER_ID_HEADER, String.valueOf(validationResponse.getUserId()))
                            .header(USERNAME_HEADER, validationResponse.getUsername());
                    
                    // 添加可选的用户信息
                    if (StrUtil.isNotBlank(validationResponse.getDeptId())) {
                        requestBuilder.header(DEPT_ID_HEADER, validationResponse.getDeptId());
                    }
                    if (validationResponse.getRoles() != null && !validationResponse.getRoles().isEmpty()) {
                        requestBuilder.header(ROLES_HEADER, String.join(",", validationResponse.getRoles()));
                    }
                    if (validationResponse.getPermissions() != null && !validationResponse.getPermissions().isEmpty()) {
                        requestBuilder.header(PERMISSIONS_HEADER, String.join(",", validationResponse.getPermissions()));
                    }
                    if (StrUtil.isNotBlank(validationResponse.getDeviceId())) {
                        requestBuilder.header(DEVICE_ID_HEADER, validationResponse.getDeviceId());
                    }
                    if (StrUtil.isNotBlank(validationResponse.getSessionId())) {
                        requestBuilder.header(SESSION_ID_HEADER, validationResponse.getSessionId());
                    }
                    
                    ServerHttpRequest modifiedRequest = requestBuilder.build();
                    ServerWebExchange modifiedExchange = exchange.mutate()
                            .request(modifiedRequest)
                            .build();
                    
                    log.debug("用户{}访问{}的身份验证成功", validationResponse.getUsername(), path);
                    
                    return chain.filter(modifiedExchange);
                })
                .onErrorResume(throwable -> {
                    log.error("令牌验证过程中发生错误: {}", throwable.getMessage());
                    return unauthorizedResponse(response, "认证服务不可用");
                });
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
package com.loveyue.common.interceptor;

import com.loveyue.common.context.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;
import java.util.UUID;

/**
 * @Description: 请求上下文拦截器
 * @Date 2025/07/13
 * @Author LoveYue
 */
@Slf4j
@Component
public class RequestContextInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<RequestContext> REQUEST_CONTEXT_HOLDER = new ThreadLocal<>();

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        try {
            RequestContext context = buildRequestContext(request);
            REQUEST_CONTEXT_HOLDER.set(context);
            log.debug("请求上下文已初始化: {}", context.getRequestId());
        } catch (Exception e) {
            log.warn("初始化请求上下文失败: {}", e.getMessage());
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        try {
            RequestContext context = REQUEST_CONTEXT_HOLDER.get();
            if (context != null) {
                log.debug("请求上下文已清理: {}", context.getRequestId());
            }
        } finally {
            REQUEST_CONTEXT_HOLDER.remove();
        }
    }

    /**
     * 获取当前请求上下文
     */
    public static RequestContext getCurrentRequestContext() {
        return REQUEST_CONTEXT_HOLDER.get();
    }

    /**
     * 构建请求上下文
     */
    private RequestContext buildRequestContext(HttpServletRequest request) {
        RequestContext context = new RequestContext();
        
        context.setRequestId(UUID.randomUUID().toString());
        context.setRequestTime(new Date());
        context.setRequestPath(request.getRequestURI());
        context.setRequestMethod(request.getMethod());
        context.setClientIp(getClientIp(request));
        context.setUserAgent(request.getHeader("User-Agent"));
        
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                context.setUserId(Long.valueOf(userIdHeader));
            } catch (NumberFormatException e) {
                log.warn("无效的用户ID格式: {}", userIdHeader);
            }
        }
        
        String usernameHeader = request.getHeader("X-Username");
        if (usernameHeader != null && !usernameHeader.isEmpty()) {
            context.setUsername(usernameHeader);
        }
        
        context.setRoles(request.getHeader("X-Roles"));
        context.setPermissions(request.getHeader("X-Permissions"));
        
        return context;
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
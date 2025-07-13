package com.loveyue.gateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义网关过滤器工厂
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Slf4j
@Component
public class CustomGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomGatewayFilterFactory.Config> {

    public CustomGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("enabled", "message");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!config.isEnabled()) {
                return chain.filter(exchange);
            }
            
            ServerHttpRequest request = exchange.getRequest();
            log.info("Custom filter processing request: {} - Message: {}", 
                    request.getURI().getPath(), config.getMessage());
            
            //TODO 在这里可以添加自定义的业务逻辑
            //TODO 例如：请求参数验证、请求转换、添加自定义头等
            
            // 添加自定义请求头
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-Custom-Filter", "processed")
                    .header("X-Filter-Message", config.getMessage())
                    .build();
            
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    @Data
    public static class Config {
        private boolean enabled = true;
        private String message = "Custom filter applied";
    }

}
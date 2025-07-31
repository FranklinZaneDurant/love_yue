package com.loveyue.gateway.client;

import com.loveyue.gateway.dto.BooleanApiResponse;
import com.loveyue.gateway.dto.TokenValidationApiResponse;
import com.loveyue.gateway.dto.TokenValidationRequest;
import com.loveyue.gateway.dto.TokenValidationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 认证服务客户端
 * 
 * @author loveyue
 * @since 2025-01-20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${auth.service.url:http://love-yue-auth}")
    private String authServiceUrl;

    /**
     * 验证令牌
     * 
     * @param token JWT令牌
     * @return 验证结果
     */
    public Mono<TokenValidationResponse> validateToken(String token) {
        TokenValidationRequest request = new TokenValidationRequest();
        request.setToken(token);

        return webClientBuilder.build()
                .post()
                .uri(authServiceUrl + "/api/auth/token/validate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TokenValidationApiResponse.class)
                .map(response -> {
                    if (response.isSuccess()) {
                        return response.getData();
                    } else {
                        log.warn("令牌验证失败: {}", response.getMessage());
                        return TokenValidationResponse.builder()
                                .valid(false)
                                .errorMessage(response.getMessage())
                                .build();
                    }
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(throwable -> {
                    log.error("调用认证服务失败: {}", throwable.getMessage());
                    return Mono.just(TokenValidationResponse.builder()
                            .valid(false)
                            .errorMessage("认证服务不可用")
                            .build());
                });
    }

    /**
     * 检查令牌黑名单
     * 
     * @param token JWT令牌
     * @return 是否在黑名单中
     */
    public Mono<Boolean> checkTokenBlacklist(String token) {
        return webClientBuilder.build()
                .get()
                .uri(authServiceUrl + "/api/auth/token/blacklist/check?token={token}", token)
                .retrieve()
                .bodyToMono(BooleanApiResponse.class)
                .map(response -> {
                    if (response.isSuccess()) {
                        return response.getData();
                    } else {
                        log.warn("检查令牌黑名单失败: {}", response.getMessage());
                        return false;
                    }
                })
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(throwable -> {
                    log.error("检查令牌黑名单失败: {}", throwable.getMessage());
                    return Mono.just(false);
                });
    }

    /**
     * API响应包装类
     */
    private static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}
package com.loveyue.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Description: Auth服务启动类
 * @Date 2025/7/31
 * @Author LoveYue
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}

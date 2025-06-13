package org.dzf.nacos.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Nacos演示控制器
 * 展示服务注册与配置中心功能
 * 
 * @author dzf
 * @since 1.0.0
 */
@RestController
@RequestMapping("/nacos")
@RefreshScope // 支持配置动态刷新
public class NacosController {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.application.name:nacos-service}")
    private String applicationName;

    @Value("${nacos.config.message:Hello from Nacos Config!}")
    private String configMessage;

    @Value("${nacos.config.version:1.0.0}")
    private String configVersion;

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", applicationName);
        result.put("port", serverPort);
        result.put("timestamp", LocalDateTime.now());
        return result;
    }

    /**
     * 获取服务信息
     */
    @GetMapping("/info")
    public Map<String, Object> getServiceInfo() {
        Map<String, Object> result = new HashMap<>();
        result.put("serviceName", applicationName);
        result.put("port", serverPort);
        result.put("message", "服务已成功注册到Nacos");
        result.put("timestamp", LocalDateTime.now());
        return result;
    }

    /**
     * 获取配置信息
     */
    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("configMessage", configMessage);
        result.put("configVersion", configVersion);
        result.put("description", "这些配置来自Nacos配置中心，支持动态刷新");
        result.put("timestamp", LocalDateTime.now());
        return result;
    }

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Nacos服务测试成功!");
        result.put("service", applicationName);
        result.put("port", serverPort);
        result.put("configMessage", configMessage);
        result.put("timestamp", LocalDateTime.now());
        return result;
    }
}
package org.dzf.nacos.controller;

import org.dzf.nacos.service.NacosConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置管理控制器
 * 展示Nacos配置中心的各种功能
 * 
 * @author dzf
 * @since 1.0.0
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private NacosConfigService nacosConfigService;

    /**
     * 获取所有配置信息
     */
    @GetMapping("/all")
    public Map<String, Object> getAllConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", LocalDateTime.now());
        result.put("data", nacosConfigService.getAllConfig());
        result.put("description", "获取所有Nacos配置信息");
        return result;
    }

    /**
     * 检查配置状态
     */
    @GetMapping("/status")
    public Map<String, Object> getConfigStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("enabled", nacosConfigService.isConfigEnabled());
        result.put("message", nacosConfigService.getConfigMessage());
        result.put("version", nacosConfigService.getConfigVersion());
        result.put("environment", nacosConfigService.getEnvironment());
        result.put("timeout", nacosConfigService.getTimeout());
        result.put("timestamp", LocalDateTime.now());
        return result;
    }

    /**
     * 验证配置完整性
     */
    @GetMapping("/validate")
    public Map<String, Object> validateConfig() {
        Map<String, Object> result = nacosConfigService.validateConfig();
        result.put("timestamp", LocalDateTime.now());
        result.put("description", "配置验证结果");
        return result;
    }

    /**
     * 获取配置详情
     */
    @GetMapping("/detail")
    public Map<String, Object> getConfigDetail() {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> configDetail = new HashMap<>();
        configDetail.put("message", nacosConfigService.getConfigMessage());
        configDetail.put("version", nacosConfigService.getConfigVersion());
        configDetail.put("environment", nacosConfigService.getEnvironment());
        configDetail.put("enabled", nacosConfigService.isConfigEnabled());
        configDetail.put("timeout", nacosConfigService.getTimeout());
        
        result.put("config", configDetail);
        result.put("features", new String[]{
            "动态配置刷新",
            "多环境配置",
            "配置版本管理",
            "配置回滚",
            "配置监听"
        });
        result.put("timestamp", LocalDateTime.now());
        result.put("description", "Nacos配置中心详细信息");
        
        return result;
    }

    /**
     * 获取配置使用说明
     */
    @GetMapping("/usage")
    public Map<String, Object> getUsageInfo() {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> usage = new HashMap<>();
        usage.put("配置刷新", "POST /actuator/refresh");
        usage.put("健康检查", "GET /actuator/health");
        usage.put("配置信息", "GET /actuator/configprops");
        usage.put("环境信息", "GET /actuator/env");
        
        Map<String, String> nacosConsole = new HashMap<>();
        nacosConsole.put("地址", "http://localhost:8848/nacos");
        nacosConsole.put("用户名", "nacos");
        nacosConsole.put("密码", "nacos");
        
        result.put("endpoints", usage);
        result.put("nacosConsole", nacosConsole);
        result.put("configFiles", new String[]{
            "nacos-service.yml",
            "nacos-service-dev.yml",
            "common-config.yml",
            "shared-config.yml"
        });
        result.put("timestamp", LocalDateTime.now());
        result.put("description", "Nacos配置中心使用说明");
        
        return result;
    }
}
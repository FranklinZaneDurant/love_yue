package org.dzf.nacos.service;

import org.dzf.nacos.config.NacosConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Nacos配置服务类
 * 演示如何使用Nacos配置中心的配置
 * 
 * @author dzf
 * @since 1.0.0
 */
@Service
public class NacosConfigService {

    @Autowired
    private NacosConfig nacosConfig;

    /**
     * 获取所有配置信息
     * 
     * @return 配置信息Map
     */
    public Map<String, Object> getAllConfig() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("message", nacosConfig.getMessage());
        configMap.put("version", nacosConfig.getVersion());
        configMap.put("environment", nacosConfig.getEnvironment());
        configMap.put("enabled", nacosConfig.isEnabled());
        configMap.put("timeout", nacosConfig.getTimeout());
        configMap.put("source", "Nacos配置中心");
        return configMap;
    }

    /**
     * 检查配置是否启用
     * 
     * @return 是否启用
     */
    public boolean isConfigEnabled() {
        return nacosConfig.isEnabled();
    }

    /**
     * 获取配置消息
     * 
     * @return 配置消息
     */
    public String getConfigMessage() {
        return nacosConfig.getMessage();
    }

    /**
     * 获取配置版本
     * 
     * @return 配置版本
     */
    public String getConfigVersion() {
        return nacosConfig.getVersion();
    }

    /**
     * 获取环境信息
     * 
     * @return 环境信息
     */
    public String getEnvironment() {
        return nacosConfig.getEnvironment();
    }

    /**
     * 获取超时时间
     * 
     * @return 超时时间
     */
    public long getTimeout() {
        return nacosConfig.getTimeout();
    }

    /**
     * 验证配置完整性
     * 
     * @return 验证结果
     */
    public Map<String, Object> validateConfig() {
        Map<String, Object> result = new HashMap<>();
        
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();
        
        if (nacosConfig.getMessage() == null || nacosConfig.getMessage().trim().isEmpty()) {
            isValid = false;
            errors.append("配置消息不能为空; ");
        }
        
        if (nacosConfig.getVersion() == null || nacosConfig.getVersion().trim().isEmpty()) {
            isValid = false;
            errors.append("配置版本不能为空; ");
        }
        
        if (nacosConfig.getTimeout() <= 0) {
            isValid = false;
            errors.append("超时时间必须大于0; ");
        }
        
        result.put("valid", isValid);
        result.put("errors", errors.toString());
        result.put("config", nacosConfig.toString());
        
        return result;
    }
}
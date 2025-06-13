package org.dzf.nacos;

import org.dzf.nacos.config.NacosConfig;
import org.dzf.nacos.service.NacosConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Nacos服务应用测试类
 * 
 * @author dzf
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
class NacosServiceApplicationTests {

    @Autowired
    private NacosConfig nacosConfig;

    @Autowired
    private NacosConfigService nacosConfigService;

    /**
     * 测试应用上下文加载
     */
    @Test
    void contextLoads() {
        assertNotNull(nacosConfig);
        assertNotNull(nacosConfigService);
    }

    /**
     * 测试Nacos配置
     */
    @Test
    void testNacosConfig() {
        assertNotNull(nacosConfig.getMessage());
        assertNotNull(nacosConfig.getVersion());
        assertNotNull(nacosConfig.getEnvironment());
        assertTrue(nacosConfig.getTimeout() > 0);
        
        System.out.println("Nacos配置测试通过:");
        System.out.println("Message: " + nacosConfig.getMessage());
        System.out.println("Version: " + nacosConfig.getVersion());
        System.out.println("Environment: " + nacosConfig.getEnvironment());
        System.out.println("Enabled: " + nacosConfig.isEnabled());
        System.out.println("Timeout: " + nacosConfig.getTimeout());
    }

    /**
     * 测试配置服务
     */
    @Test
    void testNacosConfigService() {
        Map<String, Object> allConfig = nacosConfigService.getAllConfig();
        assertNotNull(allConfig);
        assertFalse(allConfig.isEmpty());
        
        assertTrue(allConfig.containsKey("message"));
        assertTrue(allConfig.containsKey("version"));
        assertTrue(allConfig.containsKey("environment"));
        assertTrue(allConfig.containsKey("enabled"));
        assertTrue(allConfig.containsKey("timeout"));
        
        System.out.println("配置服务测试通过:");
        allConfig.forEach((key, value) -> 
            System.out.println(key + ": " + value)
        );
    }

    /**
     * 测试配置验证
     */
    @Test
    void testConfigValidation() {
        Map<String, Object> validation = nacosConfigService.validateConfig();
        assertNotNull(validation);
        assertTrue(validation.containsKey("valid"));
        assertTrue(validation.containsKey("errors"));
        assertTrue(validation.containsKey("config"));
        
        Boolean isValid = (Boolean) validation.get("valid");
        assertNotNull(isValid);
        
        System.out.println("配置验证测试通过:");
        System.out.println("Valid: " + isValid);
        System.out.println("Errors: " + validation.get("errors"));
    }

    /**
     * 测试配置属性获取
     */
    @Test
    void testConfigProperties() {
        String message = nacosConfigService.getConfigMessage();
        String version = nacosConfigService.getConfigVersion();
        String environment = nacosConfigService.getEnvironment();
        long timeout = nacosConfigService.getTimeout();
        boolean enabled = nacosConfigService.isConfigEnabled();
        
        assertNotNull(message);
        assertNotNull(version);
        assertNotNull(environment);
        assertTrue(timeout > 0);
        
        System.out.println("配置属性测试通过:");
        System.out.println("Message: " + message);
        System.out.println("Version: " + version);
        System.out.println("Environment: " + environment);
        System.out.println("Timeout: " + timeout);
        System.out.println("Enabled: " + enabled);
    }
}
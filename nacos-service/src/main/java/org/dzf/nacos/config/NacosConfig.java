package org.dzf.nacos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Nacos配置属性类
 * 用于绑定Nacos配置中心的配置项
 * 
 * @author dzf
 * @since 1.0.0
 */
@Component
@RefreshScope
@ConfigurationProperties(prefix = "nacos.config")
public class NacosConfig {

    /**
     * 配置消息
     */
    private String message = "Hello from Nacos Config!";

    /**
     * 配置版本
     */
    private String version = "1.0.0";

    /**
     * 环境标识
     */
    private String environment = "dev";

    /**
     * 是否启用功能
     */
    private boolean enabled = true;

    /**
     * 超时时间（毫秒）
     */
    private long timeout = 5000;

    // Getter and Setter methods
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "NacosConfig{" +
                "message='" + message + '\'' +
                ", version='" + version + '\'' +
                ", environment='" + environment + '\'' +
                ", enabled=" + enabled +
                ", timeout=" + timeout +
                '}';
    }
}
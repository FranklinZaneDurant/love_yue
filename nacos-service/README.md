# Nacos Service Module

基于Spring Cloud Alibaba Nacos的服务注册与配置中心模块，支持JDK 21。

## 功能特性

- ✅ 服务注册与发现
- ✅ 配置中心管理
- ✅ 动态配置刷新
- ✅ 多环境配置支持
- ✅ 健康检查
- ✅ 配置验证
- ✅ 优雅关闭

## 技术栈

- **JDK**: 21
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **Spring Cloud Alibaba**: 2022.0.0.0
- **Nacos**: 最新版本

## 快速开始

### 1. 启动Nacos服务器

```bash
# 下载Nacos
wget https://github.com/alibaba/nacos/releases/download/2.3.0/nacos-server-2.3.0.tar.gz
tar -xzf nacos-server-2.3.0.tar.gz
cd nacos/bin

# 启动Nacos（单机模式）
./startup.sh -m standalone
```

访问Nacos控制台：http://localhost:8848/nacos
- 用户名：nacos
- 密码：nacos

### 2. 配置Nacos配置中心

在Nacos控制台中创建以下配置文件：

#### 主配置文件：`nacos-service.yml`
```yaml
nacos:
  config:
    message: "Hello from Nacos Config Center!"
    version: "2.0.0"
    environment: "nacos"
    enabled: true
    timeout: 10000
```

#### 环境配置文件：`nacos-service-dev.yml`
```yaml
nacos:
  config:
    message: "Hello from Development Environment!"
    environment: "dev"
    timeout: 3000
```

#### 通用配置文件：`common-config.yml`
```yaml
logging:
  level:
    org.dzf.nacos: INFO
```

#### 共享配置文件：`shared-config.yml`
```yaml
management:
  endpoints:
    web:
      exposure:
        include: "health,info,refresh,configprops,env"
```

### 3. 启动应用

```bash
# 在项目根目录执行
mvn clean compile
mvn spring-boot:run -pl nacos-service
```

或者指定环境：
```bash
mvn spring-boot:run -pl nacos-service -Dspring-boot.run.profiles=dev
```

## API接口

### 基础接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/nacos/health` | GET | 健康检查 |
| `/nacos/info` | GET | 服务信息 |
| `/nacos/config` | GET | 配置信息 |
| `/nacos/test` | GET | 测试接口 |

### 配置管理接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/config/all` | GET | 获取所有配置 |
| `/config/status` | GET | 配置状态 |
| `/config/validate` | GET | 配置验证 |
| `/config/detail` | GET | 配置详情 |
| `/config/usage` | GET | 使用说明 |

### 管理端点

| 端点 | 描述 |
|------|------|
| `/actuator/health` | 健康检查 |
| `/actuator/info` | 应用信息 |
| `/actuator/refresh` | 刷新配置 |
| `/actuator/configprops` | 配置属性 |
| `/actuator/env` | 环境信息 |

## 配置说明

### bootstrap.yml
核心配置文件，包含Nacos连接信息和服务发现配置。

### application.yml
应用配置文件，包含服务器配置和多环境配置。

### 配置优先级
1. Nacos配置中心
2. application.yml
3. bootstrap.yml
4. 默认配置

## 动态配置刷新

### 方式一：使用@RefreshScope注解
```java
@RestController
@RefreshScope
public class ConfigController {
    @Value("${nacos.config.message}")
    private String message;
}
```

### 方式二：调用刷新端点
```bash
curl -X POST http://localhost:8080/actuator/refresh
```

### 方式三：使用@ConfigurationProperties
```java
@Component
@RefreshScope
@ConfigurationProperties(prefix = "nacos.config")
public class NacosConfig {
    // 配置属性
}
```

## 多环境配置

### 开发环境
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 测试环境
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

### 生产环境
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## 监控与健康检查

### 健康检查
```bash
curl http://localhost:8080/actuator/health
```

### 服务状态
```bash
curl http://localhost:8080/nacos/health
```

### 配置状态
```bash
curl http://localhost:8080/config/status
```

## 故障排除

### 常见问题

1. **无法连接到Nacos服务器**
   - 检查Nacos服务器是否启动
   - 确认服务器地址配置正确
   - 检查网络连接

2. **配置无法刷新**
   - 确认使用了@RefreshScope注解
   - 检查配置文件格式是否正确
   - 调用/actuator/refresh端点

3. **服务注册失败**
   - 检查服务名称配置
   - 确认命名空间和分组配置
   - 查看应用日志

### 日志配置

查看详细日志：
```yaml
logging:
  level:
    com.alibaba.nacos: DEBUG
    org.springframework.cloud.alibaba.nacos: DEBUG
```

## 扩展功能

### 自定义配置监听器
```java
@Component
public class ConfigListener {
    @NacosConfigListener(dataId = "nacos-service.yml", groupId = "DEFAULT_GROUP")
    public void onConfigChange(String configInfo) {
        // 处理配置变更
    }
}
```

### 服务发现
```java
@Autowired
private DiscoveryClient discoveryClient;

public List<ServiceInstance> getInstances(String serviceName) {
    return discoveryClient.getInstances(serviceName);
}
```

## 部署建议

### Docker部署
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/nacos-service-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Kubernetes部署
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nacos-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nacos-service
  template:
    metadata:
      labels:
        app: nacos-service
    spec:
      containers:
      - name: nacos-service
        image: nacos-service:1.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
```

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

MIT License

## 联系方式

- 作者：dzf
- 邮箱：your-email@example.com
- 项目地址：https://github.com/your-username/love_yue
# Love Yue 认证服务 (Auth Service)

## 概述

Love Yue 认证服务是一个基于 Spring Boot 和 JWT 的用户认证和授权微服务，提供完整的用户登录、令牌管理、权限控制等功能。

## 主要功能

### 🔐 认证功能
- 用户登录/登出
- JWT 令牌生成和验证
- 刷新令牌机制
- 多设备登录支持
- 单点登录检查

### 👤 用户管理
- 用户信息管理
- 用户状态控制（启用/禁用/锁定）
- 密码安全策略
- 登录失败锁定机制

### 🛡️ 安全特性
- 密码加密存储（BCrypt）
- 登录日志记录
- 可疑登录检测
- IP 白名单支持
- 设备指纹验证
- CSRF 保护

### 🎯 权限控制
- 基于角色的访问控制（RBAC）
- 用户角色关联管理
- 权限验证
- 临时令牌支持

### 📊 监控和管理
- 令牌统计信息
- 在线用户监控
- 定时任务清理
- 健康检查
- Prometheus 指标

## 技术架构

### 技术栈
- **框架**: Spring Boot 3.x
- **安全**: Spring Security 6.x
- **数据库**: PostgreSQL + Spring Data JPA
- **缓存**: Redis
- **令牌**: JWT (JJWT)
- **服务发现**: Eureka Client
- **文档**: SpringDoc OpenAPI 3
- **监控**: Spring Boot Actuator + Micrometer

### 核心组件

```
├── controller/          # REST API 控制器
├── service/            # 业务逻辑层
├── repository/         # 数据访问层
├── entity/            # 实体类
├── dto/               # 数据传输对象
├── util/              # 工具类
├── config/            # 配置类
├── exception/         # 异常处理
└── task/              # 定时任务
```

## 数据模型

### 核心实体

#### User (用户)
- 用户基本信息
- 登录凭据
- 状态管理
- 安全设置

#### Role (角色)
- 角色定义
- 权限关联

#### UserRole (用户角色关联)
- 多对多关系
- 授权时间
- 过期控制

#### UserToken (用户令牌)
- JWT 令牌管理
- 设备信息
- 状态跟踪

#### UserLoginLog (登录日志)
- 登录记录
- 设备信息
- 位置信息

## API 接口

### 认证接口

```http
# 用户登录
POST /api/auth/login

# 用户登出
POST /api/auth/logout

# 刷新令牌
POST /api/auth/refresh

# 验证令牌
POST /api/auth/validate
```

### 用户信息接口

```http
# 获取用户信息
GET /api/auth/userEntity-info

# 获取用户角色
GET /api/auth/userEntity-roles

# 获取用户权限
GET /api/auth/userEntity-permissions
```

### 令牌管理接口

```http
# 撤销令牌
POST /api/auth/revoke

# 撤销用户所有令牌
POST /api/auth/revoke-all/{userId}

# 强制用户下线
POST /api/auth/force-offline/{userId}
```

### 监控接口

```http
# 检查在线状态
GET /api/auth/online-status/{userId}

# 获取活跃令牌
GET /api/auth/active-tokens/{userId}

# 获取统计信息
GET /api/auth/statistics
```

## 配置说明

### JWT 配置

```yaml
auth:
  jwt:
    secret: your-jwt-secret-key
    access-token-expire-minutes: 120
    refresh-token-expire-days: 7
    issuer: loveyue-auth
    audience: loveyue-app
```

### 登录配置

```yaml
auth:
  login:
    max-attempts: 5
    lock-duration-minutes: 30
    allow-multiple-devices: true
    captcha-enabled: false
```

### 安全配置

```yaml
auth:
  security:
    suspicious-login-detection: true
    remote-location-detection: true
    csrf-protection-enabled: true
```

## 部署指南

### 环境要求
- Java 17+
- PostgreSQL 12+
- Redis 6+
- Maven 3.8+

### 构建和运行

```bash
# 构建项目
mvn clean package

# 运行服务
java -jar target/love-yue-auth-1.0.0.jar

# 或使用 Maven 运行
mvn spring-boot:run
```

### Docker 部署

```dockerfile
FROM openjdk:17-jre-slim
COPY target/love-yue-auth-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 环境变量

```bash
# 数据库配置
DB_HOST=localhost
DB_PORT=5432
DB_NAME=loveyue_auth
DB_USERNAME=loveyue
DB_PASSWORD=loveyue123

# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT 配置
JWT_SECRET=your-very-secure-jwt-secret-key
```

## 监控和运维

### 健康检查

```http
GET /actuator/health
```

### 指标监控

```http
GET /actuator/metrics
GET /actuator/prometheus
```

### 日志配置

服务支持结构化日志输出，可配置不同级别的日志记录：

- 登录/登出事件
- 令牌操作记录
- 安全事件告警
- 性能指标

## 安全最佳实践

### 生产环境建议

1. **JWT 密钥管理**
   - 使用强随机密钥
   - 定期轮换密钥
   - 安全存储密钥

2. **密码策略**
   - 启用密码复杂度检查
   - 设置合理的过期时间
   - 强制定期更换

3. **访问控制**
   - 启用 IP 白名单
   - 配置设备指纹验证
   - 监控异常登录

4. **令牌管理**
   - 设置合理的过期时间
   - 限制活跃令牌数量
   - 定期清理过期令牌

## 故障排查

### 常见问题

1. **令牌验证失败**
   - 检查 JWT 密钥配置
   - 确认令牌未过期
   - 验证令牌格式

2. **登录失败**
   - 检查用户状态
   - 确认密码正确
   - 查看登录日志

3. **数据库连接问题**
   - 检查数据库配置
   - 确认网络连通性
   - 验证用户权限

### 日志分析

```bash
# 查看登录日志
grep "LOGIN" logs/auth-service.log

# 查看错误日志
grep "ERROR" logs/auth-service.log

# 查看令牌操作
grep "TOKEN" logs/auth-service.log
```

## 开发指南

### 本地开发

1. 克隆项目
2. 配置数据库和 Redis
3. 修改 `application-dev.yml`
4. 运行 `AuthApplication`

### 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify

# 生成测试报告
mvn jacoco:report
```

### API 文档

启动服务后访问：
- Swagger UI: http://localhost:8081/auth/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/auth/v3/api-docs

## 版本历史

- **v1.0.0** - 初始版本
  - 基础认证功能
  - JWT 令牌管理
  - 用户角色权限
  - 登录日志记录

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目主页: https://github.com/loveyue/love-yue-auth
- 问题反馈: https://github.com/loveyue/love-yue-auth/issues
- 邮箱: support@loveyue.com
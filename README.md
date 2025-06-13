# Love Yue Project

基于JDK 21的Spring Cloud Alibaba微服务项目，采用多模块架构设计。

## 项目结构

```
love_yue/
├── pom.xml                 # 父级POM文件
├── README.md              # 项目说明文档
├── nacos-service/         # Nacos服务注册与配置中心模块
│   ├── pom.xml
│   ├── README.md
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── org/dzf/nacos/
│       │   │       ├── NacosServiceApplication.java
│       │   │       ├── config/
│       │   │       │   └── NacosConfig.java
│       │   │       ├── controller/
│       │   │       │   ├── NacosController.java
│       │   │       │   └── ConfigController.java
│       │   │       └── service/
│       │   │           └── NacosConfigService.java
│       │   └── resources/
│       │       ├── bootstrap.yml
│       │       └── application.yml
│       └── test/
│           └── java/
│               └── org/dzf/nacos/
│                   └── NacosServiceApplicationTests.java
└── .gitignore
```

## 技术栈

- **JDK**: 21
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **Spring Cloud Alibaba**: 2022.0.0.0
- **Maven**: 3.6+

## 模块说明

### nacos-service
Spring Cloud Alibaba Nacos服务注册与配置中心模块，提供：
- 服务注册与发现
- 配置中心管理
- 动态配置刷新
- 多环境配置支持
- 健康检查和监控

详细说明请查看：[nacos-service/README.md](nacos-service/README.md)

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- Nacos Server 2.3.0+

### 1. 克隆项目

```bash
git clone <repository-url>
cd love_yue
```

### 2. 编译项目

```bash
mvn clean compile
```

### 3. 启动Nacos服务器

```bash
# 下载并启动Nacos
wget https://github.com/alibaba/nacos/releases/download/2.3.0/nacos-server-2.3.0.tar.gz
tar -xzf nacos-server-2.3.0.tar.gz
cd nacos/bin
./startup.sh -m standalone
```

### 4. 启动服务

```bash
# 启动Nacos服务模块
mvn spring-boot:run -pl nacos-service
```

### 5. 验证服务

```bash
# 健康检查
curl http://localhost:8080/nacos/health

# 配置信息
curl http://localhost:8080/config/all
```

## 开发指南

### 添加新模块

1. 在根目录创建新模块文件夹
2. 创建模块的pom.xml文件
3. 在父POM中添加模块引用
4. 实现模块功能

### 配置管理

- 本地配置：`application.yml`
- 引导配置：`bootstrap.yml`
- Nacos配置：通过Nacos控制台管理

### 多环境部署

```bash
# 开发环境
mvn spring-boot:run -pl nacos-service -Dspring-boot.run.profiles=dev

# 测试环境
mvn spring-boot:run -pl nacos-service -Dspring-boot.run.profiles=test

# 生产环境
mvn spring-boot:run -pl nacos-service -Dspring-boot.run.profiles=prod
```

## 监控与管理

### Nacos控制台
- 地址：http://localhost:8848/nacos
- 用户名：nacos
- 密码：nacos

### 应用监控端点
- 健康检查：http://localhost:8080/actuator/health
- 应用信息：http://localhost:8080/actuator/info
- 配置刷新：POST http://localhost:8080/actuator/refresh

## 测试

### 运行单元测试

```bash
# 运行所有测试
mvn test

# 运行特定模块测试
mvn test -pl nacos-service
```

### 集成测试

```bash
# 启动服务后运行集成测试
mvn verify
```

## 部署

### 本地部署

```bash
# 打包应用
mvn clean package

# 运行JAR文件
java -jar nacos-service/target/nacos-service-1.0-SNAPSHOT.jar
```

### Docker部署

```bash
# 构建Docker镜像
docker build -t love-yue/nacos-service:1.0 nacos-service/

# 运行容器
docker run -p 8080:8080 love-yue/nacos-service:1.0
```

### Kubernetes部署

```bash
# 应用Kubernetes配置
kubectl apply -f k8s/
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 版本历史

- **v1.0.0** - 初始版本
  - 添加Nacos服务注册与配置中心模块
  - 支持JDK 21
  - 多环境配置支持

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 作者：dzf
- 邮箱：your-email@example.com
- 项目地址：https://github.com/your-username/love_yue

## 致谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Cloud](https://spring.io/projects/spring-cloud)
- [Spring Cloud Alibaba](https://github.com/alibaba/spring-cloud-alibaba)
- [Nacos](https://nacos.io/)
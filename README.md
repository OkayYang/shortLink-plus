# ShortLink-Plus 短链接服务平台

<div align="center">

![ShortLink-Plus](https://img.shields.io/badge/ShortLink--Plus-v1.0--SNAPSHOT-blue.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen.svg)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.3-brightgreen.svg)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)

一个基于Spring Cloud微服务架构的企业级短链接服务平台，集成AI智能助手，提供完整的短链接生成、管理、统计和分析功能。

[在线演示](https://github.com/OkayYang/shortlink) | [快速开始](#快速开始) | [API文档](#api接口) | [部署指南](#部署指南)

</div>

## ✨ 核心特性

### 🔗 短链接服务
- **智能生成算法**: 基于UUID + 布隆过滤器的短链接生成算法
- **防缓存击穿**: 采用分布式锁 + 双重检查机制，确保高并发下的数据一致性
- **多域名支持**: 支持自定义域名，提供更好的品牌体验
- **有效期管理**: 灵活的链接有效期设置，自动过期处理
- **匿名用户支持**: 无需注册即可生成短链接，每日限额保护

### 📊 数据统计分析
- **实时访问统计**: PV（页面访问量）、UV（独立访客）、UIP（独立IP）统计
- **多维度分析**: 按小时、星期维度进行访问数据分析
- **异步统计处理**: 基于RocketMQ的异步消息处理，不影响重定向性能
- **用户数据看板**: 个人短链接数据统计和可视化展示

### 🤖 AI智能助手
- **集成Spring AI**: 基于OpenAI API的智能聊天助手
- **工具调用能力**: 支持天气查询、日期时间、短链接生成等工具
- **流式响应**: Server-Sent Events实现的流式对话体验
- **智能内容生成**: 自动抓取网页内容，生成推广文案

### 🏗️ 微服务架构
- **服务网关**: Spring Cloud Gateway统一入口
- **服务发现**: Nacos注册中心和配置中心
- **认证授权**: JWT + 统一认证服务
- **负载均衡**: Spring Cloud LoadBalancer
- **熔断限流**: 内置服务保护机制

### 🚀 高性能设计
- **数据库分片**: ShardingSphere实现的分库分表
- **Redis缓存**: 多级缓存策略，提升访问性能
- **布隆过滤器**: 有效防止缓存穿透
- **异步处理**: RocketMQ消息队列处理统计数据

## 🏛️ 技术架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端应用                               │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                   网关服务 (Gateway)                         │
│              Spring Cloud Gateway                           │
└─────────────────────┬───────────────────────────────────────┘
                      │
        ┌─────────────┼─────────────┐
        │             │             │
┌───────▼──────┐ ┌───▼────┐ ┌─────▼─────┐ ┌──────────────┐
│   控制台服务   │ │认证服务 │ │ 系统服务   │ │   AI代理服务  │
│  (Console)   │ │ (Auth) │ │(System)   │ │   (Agent)    │
│              │ │        │ │           │ │              │
│ 用户管理      │ │JWT认证 │ │短链接核心  │ │ Spring AI    │
│ 分组管理      │ │用户登录 │ │访问统计    │ │ 工具调用      │
│ 短链接代理     │ │        │ │数据分析    │ │ 智能对话      │
└──────────────┘ └────────┘ └───────────┘ └──────────────┘
        │             │             │             │
        └─────────────┼─────────────┼─────────────┘
                      │             │
┌─────────────────────▼─────────────▼─────────────────────────┐
│                    服务注册中心                               │
│                    Nacos Server                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    数据存储层                                │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐     │
│  │    MySQL     │ │    Redis     │ │    RocketMQ     │     │
│  │              │ │              │ │                  │     │
│  │ 分库分表      │ │ 缓存存储      │ │ 消息队列          │     │
│  │ 数据持久化     │ │ 分布式锁      │ │ 异步处理          │     │
│  └──────────────┘ └──────────────┘ └──────────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

## 🛠️ 技术栈

| 技术栈 | 版本 | 说明 |
|--------|------|------|
| **基础框架** |
| Java | 17 | 编程语言 |
| Spring Boot | 3.3.0 | 基础开发框架 |
| Spring Cloud | 2023.0.3 | 微服务框架 |
| Spring Cloud Alibaba | 2023.0.1.2 | 阿里云微服务套件 |
| **数据存储** |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.0+ | 缓存数据库 |
| MyBatis Plus | 3.5.5 | ORM框架 |
| ShardingSphere | 5.5.2 | 分库分表中间件 |
| **微服务组件** |
| Nacos | 2.3.0+ | 注册中心/配置中心 |
| Spring Cloud Gateway | - | 服务网关 |
| OpenFeign | - | 服务调用 |
| **消息队列** |
| RocketMQ | 5.1.0+ | 消息中间件 |
| **AI能力** |
| Spring AI | 1.0.0 | AI集成框架 |
| OpenAI API | - | 大语言模型接口 |
| **工具库** |
| Hutool | 5.8.26 | Java工具类库 |
| Redisson | 3.21.3 | 分布式锁 |
| JWT | 0.9.1 | 认证令牌 |
| Lombok | 1.18.34 | 代码简化 |

## 📋 项目结构

```
shortlink/
├── shortlink-console/          # 控制台服务 - 用户管理和短链接代理
├── shortlink-system/           # 系统服务 - 短链接核心业务
├── shortlink-gateway/          # 网关服务 - 统一入口和路由
├── shortlink-auth/             # 认证服务 - JWT认证和用户登录
├── shortlink-agent/            # AI代理服务 - 智能助手和工具调用
├── shortlink-common/           # 公共模块
│   ├── shortlink-common-core/      # 核心工具和异常处理
│   ├── shortlink-common-redis/     # Redis配置和服务
│   └── shortlink-common-security/  # 安全配置
└── docker/                     # Docker部署配置
    ├── mysql/                      # MySQL配置和初始化脚本
    ├── redis/                      # Redis配置
    ├── nacos/                      # Nacos配置
    └── docker-compose.yml          # 容器编排配置
```

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.3.0+
- RocketMQ 5.1.0+

### 本地开发

1. **克隆项目**
```bash
git clone https://github.com/OkayYang/shortlink.git
cd shortlink
```

2. **启动基础服务**
```bash
# 启动 MySQL、Redis、Nacos、RocketMQ
# 可以使用Docker Compose快速启动
cd docker
docker-compose up -d mysql redis nacos rocketmq
```

3. **配置数据库**
```bash
# 执行数据库初始化脚本
mysql -u root -p < docker/mysql/init/01-init-tables.sql
```

4. **配置应用**
```bash
# 修改各服务的application.yml配置文件
# 配置数据库连接、Redis连接、Nacos地址等
```

5. **启动服务**
```bash
# 按顺序启动各个服务
mvn clean compile
# 1. 启动认证服务
cd shortlink-auth && mvn spring-boot:run

# 2. 启动系统服务
cd shortlink-system && mvn spring-boot:run

# 3. 启动控制台服务
cd shortlink-console && mvn spring-boot:run

# 4. 启动网关服务
cd shortlink-gateway && mvn spring-boot:run

# 5. 启动AI代理服务（可选）
cd shortlink-agent && mvn spring-boot:run
```

### Docker部署

1. **构建项目**
```bash
mvn clean package -DskipTests
```

2. **使用Docker Compose部署**
```bash
cd docker
./copy-jars.sh      # 复制JAR包
./build-images.sh   # 构建镜像
docker-compose up -d
```

## 🔧 配置说明

### 核心配置

#### 数据库配置 (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shortlink?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
    username: root
    password: your_password
```

#### Redis配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your_password
```

#### Nacos配置
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
```

#### AI配置 (shortlink-agent)
```yaml
spring:
  ai:
    openai:
      api-key: your_openai_api_key
      base-url: https://api.openai.com
```

## 📚 API接口

### 短链接管理

#### 创建短链接
```http
POST /short-link/create
Content-Type: application/json

{
    "originUrl": "https://www.example.com",
    "domain": "short.ly",
    "gid": "group001",
    "expireTime": "2024-12-31T23:59:59",
    "describe": "示例短链接"
}
```

#### 短链接重定向
```http
GET /{shortUri}
```

#### 获取访问统计
```http
GET /short-link/stats/user?username=testuser
```

### AI助手

#### 智能对话
```http
POST /agent/chat/completions
Content-Type: application/json

{
    "message": "请帮我生成一个短链接：https://blog.example.com",
    "model": "gpt-3.5-turbo"
}
```

## 📊 监控和统计

### 访问统计指标

- **PV (Page View)**: 页面访问量
- **UV (Unique Visitor)**: 独立访客数
- **UIP (Unique IP)**: 独立IP数
- **按时段统计**: 24小时访问分布
- **按星期统计**: 7天访问分布

### 性能监控

- **缓存命中率**: Redis缓存性能
- **数据库连接**: 连接池使用情况
- **消息队列**: RocketMQ消息处理状态
- **服务健康**: 各微服务健康状态

## 🔒 安全特性

### 认证授权
- JWT Token认证
- 用户登录/注册
- 权限控制

### 防护机制
- 布隆过滤器防缓存穿透
- 分布式锁防缓存击穿
- 限流保护
- SQL注入防护

### 数据安全
- 敏感信息加密
- 数据备份策略
- 访问日志记录

## 🎯 最佳实践

### 性能优化
1. **缓存策略**: 多级缓存，合理设置过期时间
2. **数据库优化**: 分库分表，索引优化
3. **异步处理**: 统计数据异步处理，提升响应速度
4. **连接池**: 合理配置数据库和Redis连接池

### 高可用部署
1. **服务集群**: 多实例部署关键服务
2. **负载均衡**: 使用Nginx或Spring Cloud Gateway
3. **故障转移**: 配置服务降级和熔断
4. **监控告警**: 完善的监控和告警机制

## 🤝 贡献指南

我们欢迎所有形式的贡献，包括但不限于：

- 🐛 Bug报告
- 🚀 新功能建议
- 📝 文档改进
- 💡 代码优化

### 参与贡献

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📄 开源协议

本项目采用 [Apache License 2.0](LICENSE) 协议开源。

## 👥 团队

- **项目维护者**: [OkayYang](https://github.com/OkayYang)
- **技术支持**: 欢迎提交Issue或加入讨论群

## 📞 联系我们

- **项目地址**: [https://github.com/OkayYang/shortlink](https://github.com/OkayYang/shortlink)
- **问题反馈**: [Issues](https://github.com/OkayYang/shortlink/issues)
- **技术讨论**: [Discussions](https://github.com/OkayYang/shortlink/discussions)

## 🙏 致谢

感谢以下开源项目的支持：

- [Spring Framework](https://spring.io/) - 企业级Java开发框架
- [Spring Cloud](https://spring.io/projects/spring-cloud) - 微服务开发框架
- [MyBatis Plus](https://baomidou.com/) - 优秀的ORM框架
- [Hutool](https://hutool.cn/) - Java工具类库
- [Nacos](https://nacos.io/) - 动态服务发现和配置管理
- [RocketMQ](https://rocketmq.apache.org/) - 分布式消息中间件

---

<div align="center">

**如果这个项目对您有帮助，请给个 ⭐ Star 支持一下！**

Made with ❤️ by [ShortLink-Plus Team](https://github.com/OkayYang)

</div> 
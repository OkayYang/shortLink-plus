# ShortLink Docker部署

本目录包含了ShortLink项目的Docker部署配置，每个服务都作为独立的容器运行。

## 部署流程

### 1. 本地构建JAR包

首先在本地构建项目的JAR包：

```bash
# 在项目根目录执行
mvn clean package -DskipTests
```

### 2. 复制JAR包到Docker目录

使用提供的脚本复制JAR包：

```bash
cd docker
./copy-jars.sh
```

这个脚本会将本地构建好的JAR包复制到docker/jar目录下。

### 3. 更新JAR包中的配置

使用提供的脚本更新JAR包中的配置，使其使用Docker容器中的Redis和MySQL：

```bash
./update-config.sh
```

### 4. 构建Docker镜像

使用提供的脚本构建Docker镜像：

```bash
./build-images.sh
```

### 5. 启动服务

```bash
docker compose  up -d
```

### 6. 停止服务

```bash
docker-compose down
```

## 目录结构

```
docker/
├── auth/               # 认证服务相关文件
│   └── Dockerfile      # 认证服务Docker构建文件
├── console/            # 控制台服务相关文件
│   └── Dockerfile      # 控制台服务Docker构建文件
├── gateway/            # 网关服务相关文件
│   └── Dockerfile      # 网关服务Docker构建文件
├── jar/                # JAR包存放目录
│   ├── auth.jar        # 认证服务JAR包
│   ├── console.jar     # 控制台服务JAR包
│   ├── gateway.jar     # 网关服务JAR包
│   └── system.jar      # 系统服务JAR包
├── mysql/              # MySQL相关文件
│   ├── data/           # MySQL数据目录
│   └── init/           # MySQL初始化脚本目录
├── nacos/              # Nacos相关文件
│   ├── conf/           # Nacos配置文件目录
│   ├── logs/           # Nacos日志目录
│   └── Dockerfile      # Nacos Docker构建文件
├── redis/              # Redis相关文件
│   └── data/           # Redis数据目录
├── system/             # 系统服务相关文件
│   └── Dockerfile      # 系统服务Docker构建文件
├── .dockerignore       # Docker忽略文件
├── build-images.sh     # 构建Docker镜像的脚本
├── copy-jars.sh        # 复制JAR包的脚本
├── docker-compose.yml  # Docker Compose主配置文件
└── README.md           # 本文档
```

## 服务端口

| 服务名称 | 端口号  |
|---------|------|
| 网关服务 | 8080 |
| 控制台服务 | 9001 |
| 系统服务 | 9002 |
| 认证服务 | 9200 |
| Nacos | 8848 |
| MySQL | 3306 |
| Redis | 6379 |

## 默认账号

- 用户名: admin
- 密码: admin123

## 环境变量配置

可以通过修改docker-compose.yml文件中的environment部分来配置环境变量。

主要的环境变量包括：

- SPRING_DATASOURCE_URL：数据库连接URL
- SPRING_DATASOURCE_USERNAME：数据库用户名
- SPRING_DATASOURCE_PASSWORD：数据库密码
- SPRING_REDIS_HOST：Redis主机名
- SPRING_REDIS_PORT：Redis端口
- SPRING_CLOUD_NACOS_DISCOVERY_SERVER-ADDR：Nacos服务发现地址
- SPRING_CLOUD_NACOS_CONFIG_SERVER-ADDR：Nacos配置中心地址
- SPRING_PROFILES_ACTIVE：Spring配置文件激活的环境
- JAVA_OPTS：Java虚拟机参数

## 数据持久化

所有数据都会持久化存储在以下目录：

- MySQL数据：./mysql/data
- Redis数据：./redis/data
- Nacos日志：./nacos/logs
- 服务日志：./*/logs

## 容器间通信

所有服务都在同一个Docker网络(shortlink-network)中，可以通过服务名称相互访问：

- MySQL: mysql:3306
- Redis: redis:6379
- Nacos: nacos:8848
- 网关: gateway:8080
- 认证服务: auth:9200
- 控制台服务: console:9001
- 系统服务: system:9002 
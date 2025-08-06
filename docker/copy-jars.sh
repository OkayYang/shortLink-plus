#!/bin/bash
set -e

# 脚本说明
echo "=== ShortLink JAR包复制脚本 ==="
echo "此脚本将本地编译好的JAR包复制到Docker部署目录"

# 检查项目根目录
if [ ! -d "../shortlink-gateway" ] || [ ! -d "../shortlink-auth" ] || [ ! -d "../shortlink-console" ] || [ ! -d "../shortlink-system" ]; then
    echo "错误: 请在项目根目录下的docker目录中运行此脚本"
    exit 1
fi

# 创建jar目录（如果不存在）
mkdir -p jar

# 复制各个服务的jar包
echo "正在复制JAR包..."

# 网关服务
if [ -f "../shortlink-gateway/target/shortlink-gateway.jar" ]; then
    cp ../shortlink-gateway/target/shortlink-gateway.jar jar/gateway.jar
    echo "✓ 网关服务JAR包已复制"
else
    echo "✗ 网关服务JAR包不存在，请先构建项目"
    exit 1
fi

# 认证服务
if [ -f "../shortlink-auth/target/shortlink-auth.jar" ]; then
    cp ../shortlink-auth/target/shortlink-auth.jar jar/auth.jar
    echo "✓ 认证服务JAR包已复制"
else
    echo "✗ 认证服务JAR包不存在，请先构建项目"
    exit 1
fi

# 控制台服务
if [ -f "../shortlink-console/target/shortlink-console.jar" ]; then
    cp ../shortlink-console/target/shortlink-console.jar jar/console.jar
    echo "✓ 控制台服务JAR包已复制"
else
    echo "✗ 控制台服务JAR包不存在，请先构建项目"
    exit 1
fi

# 系统服务
if [ -f "../shortlink-system/target/shortlink-system.jar" ]; then
    cp ../shortlink-system/target/shortlink-system.jar jar/system.jar
    echo "✓ 系统服务JAR包已复制"
else
    echo "✗ 系统服务JAR包不存在，请先构建项目"
    exit 1
fi

echo "所有JAR包复制完成！"
echo "您现在可以使用 docker-compose 启动服务了"
echo "提示: 运行 'docker-compose up -d' 启动所有服务" 
#!/bin/bash
set -e

# 脚本说明
echo "=== ShortLink Docker镜像构建脚本 ==="
echo "此脚本将构建所有服务的Docker镜像"

# 检查jar目录是否存在
if [ ! -d "jar" ]; then
    echo "错误: jar目录不存在，请先运行copy-jars.sh脚本"
    exit 1
fi

# 检查jar文件是否存在
if [ ! -f "jar/gateway.jar" ] || [ ! -f "jar/auth.jar" ] || [ ! -f "jar/console.jar" ] || [ ! -f "jar/system.jar" ]; then
    echo "错误: 部分JAR包不存在，请先运行copy-jars.sh脚本"
    exit 1
fi

# 构建所有服务的Docker镜像
echo "正在构建Docker镜像..."

# Nacos服务
echo "构建Nacos服务镜像..."
docker build -t shortlink-nacos:latest -f nacos/Dockerfile nacos
echo "✓ Nacos服务镜像构建完成"

# 网关服务
echo "构建网关服务镜像..."
docker build -t shortlink-gateway:latest -f gateway/Dockerfile .
echo "✓ 网关服务镜像构建完成"

# 认证服务
echo "构建认证服务镜像..."
docker build -t shortlink-auth:latest -f auth/Dockerfile .
echo "✓ 认证服务镜像构建完成"

# 控制台服务
echo "构建控制台服务镜像..."
docker build -t shortlink-console:latest -f console/Dockerfile .
echo "✓ 控制台服务镜像构建完成"

# 系统服务
echo "构建系统服务镜像..."
docker build -t shortlink-system:latest -f system/Dockerfile .
echo "✓ 系统服务镜像构建完成"

echo "所有Docker镜像构建完成！"
echo "您现在可以使用 docker compose up -d 启动服务了"
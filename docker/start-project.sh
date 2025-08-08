#!/bin/bash

# ShortLink项目启动脚本

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查Docker是否运行
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        log_error "Docker未运行，请先启动Docker"
        exit 1
    fi
}

# 创建必要的目录
create_directories() {
    log_info "创建必要的目录..."
    mkdir -p rocketmq/namesrv/{logs,store}
    mkdir -p rocketmq/broker/{logs,store}
    mkdir -p {mysql,redis,nacos,gateway,auth,console,system}/logs
}

# 启动项目
start_project() {
    log_info "启动ShortLink项目..."
    
    # 1. 启动基础设施
    log_info "启动基础设施服务(MySQL, Redis, Nacos)..."
    docker-compose up -d mysql redis nacos
    
    # 等待基础设施启动
    log_info "等待基础设施服务启动(15秒)..."
    sleep 15
    
    # 2. 启动RocketMQ
    log_info "启动RocketMQ服务..."
    docker-compose up -d rocketmq rocketmq-broker rocketmq-console
    
    # 等待RocketMQ启动
    log_info "等待RocketMQ服务启动(10秒)..."
    sleep 10
    
    # 3. 启动应用服务
    log_info "启动应用服务..."
    docker-compose up -d gateway auth console system
    
    log_info "项目启动完成！"
    echo
    log_info "服务访问地址："
    echo "  Gateway网关: http://localhost:8080"
    echo "  Console控制台: http://localhost:9001"  
    echo "  Auth认证服务: http://localhost:9200"
    echo "  System系统服务: http://localhost:9002"
    echo "  RocketMQ控制台: http://localhost:8180"
    echo "  Nacos控制台: http://localhost:8848 (用户名/密码: nacos/nacos)"
}

# 停止项目
stop_project() {
    log_info "停止ShortLink项目..."
    docker-compose down
    log_info "项目已停止"
}

# 查看状态
show_status() {
    log_info "服务状态："
    docker-compose ps
}

# 查看日志
show_logs() {
    if [ -n "$1" ]; then
        log_info "显示服务 $1 的日志..."
        docker-compose logs -f "$1"
    else
        log_info "显示所有服务的日志..."
        docker-compose logs -f
    fi
}

# 显示帮助
show_help() {
    echo "ShortLink项目管理脚本"
    echo
    echo "用法: $0 [命令] [服务名]"
    echo
    echo "命令:"
    echo "  start    启动整个项目"
    echo "  stop     停止整个项目"
    echo "  status   显示服务状态"
    echo "  logs     显示日志 (可指定服务名)"
    echo "  help     显示此帮助信息"
    echo
    echo "示例:"
    echo "  $0 start                # 启动项目"
    echo "  $0 status               # 查看状态"
    echo "  $0 logs                 # 查看所有日志"
    echo "  $0 logs rocketmq-broker # 查看指定服务日志"
}

# 主函数
main() {
    check_docker
    
    case "${1:-help}" in
        start)
            create_directories
            start_project
            ;;
        stop)
            stop_project
            ;;
        status)
            show_status
            ;;
        logs)
            show_logs "$2"
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            log_error "未知命令: $1"
            echo
            show_help
            exit 1
            ;;
    esac
}

# 运行主函数
main "$@"

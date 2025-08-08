#!/bin/bash

# RocketMQ 测试脚本
# 用于测试 RocketMQ 的发送和接收能力
# Author: ShortLink Team
# Date: $(date +%Y-%m-%d)

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置参数
NAMESERVER="localhost:9876"
ROCKETMQ_CONTAINER="shortlink-rocketmq"
BROKER_CONTAINER="shortlink-rocketmq-broker"
TOPIC="test_topic"
CONSUMER_GROUP="test_consumer_group"
PRODUCER_GROUP="test_producer_group"
MESSAGE_COUNT=10

# 打印带颜色的消息
print_msg() {
    local color=$1
    local message=$2
    echo -e "${color}[$(date '+%Y-%m-%d %H:%M:%S')] ${message}${NC}"
}

print_success() {
    print_msg $GREEN "✓ $1"
}

print_error() {
    print_msg $RED "✗ $1"
}

print_info() {
    print_msg $BLUE "ℹ $1"
}

print_warning() {
    print_msg $YELLOW "⚠ $1"
}

# 检查 Docker 容器状态
check_container_status() {
    local container_name=$1
    local status=$(docker inspect --format='{{.State.Status}}' $container_name 2>/dev/null || echo "not_found")
    
    if [ "$status" = "running" ]; then
        print_success "$container_name 容器运行正常"
        return 0
    elif [ "$status" = "not_found" ]; then
        print_error "$container_name 容器不存在"
        return 1
    else
        print_error "$container_name 容器状态异常: $status"
        return 1
    fi
}

# 检查容器内存状态
check_container_memory() {
    local container_name=$1
    local oom_killed=$(docker inspect --format='{{.State.OOMKilled}}' $container_name 2>/dev/null || echo "unknown")
    
    if [ "$oom_killed" = "true" ]; then
        print_error "$container_name 容器被 OOM Killer 终止"
        return 1
    elif [ "$oom_killed" = "false" ]; then
        print_success "$container_name 容器内存状态正常"
        return 0
    else
        print_warning "$container_name 容器内存状态未知"
        return 0
    fi
}

# 检查 RocketMQ 服务连通性
check_rocketmq_connectivity() {
    print_info "检查 RocketMQ 服务连通性..."
    
    # 检查 NameServer
    if docker exec $ROCKETMQ_CONTAINER sh mqadmin clusterList -n localhost:9876 >/dev/null 2>&1; then
        print_success "NameServer 连通性正常"
    else
        print_error "NameServer 连通性异常"
        return 1
    fi
    
    # 检查 Broker
    if docker exec $ROCKETMQ_CONTAINER sh mqadmin brokerStatus -n localhost:9876 -b $BROKER_CONTAINER:10911 >/dev/null 2>&1; then
        print_success "Broker 连通性正常"
    else
        print_warning "Broker 连通性检查失败，但可能是正常的（权限问题）"
    fi
}

# 创建测试 Topic
create_test_topic() {
    print_info "创建测试 Topic: $TOPIC"
    
    if docker exec $ROCKETMQ_CONTAINER sh mqadmin updateTopic \
        -n localhost:9876 \
        -t $TOPIC \
        -c DefaultCluster \
        -r 4 \
        -w 4 >/dev/null 2>&1; then
        print_success "测试 Topic 创建成功"
    else
        print_warning "测试 Topic 创建失败，可能已存在"
    fi
}

# 发送测试消息
send_test_messages() {
    print_info "发送 $MESSAGE_COUNT 条测试消息..."
    
    local success_count=0
    local failed_count=0
    
    for i in $(seq 1 $MESSAGE_COUNT); do
        local message_body="Test message $i - Timestamp: $(date '+%Y-%m-%d %H:%M:%S') - Random: $RANDOM"
        local message_key="test_key_$i"
        local message_tag="TEST"
        
        if docker exec $ROCKETMQ_CONTAINER sh mqadmin sendMessage \
            -n localhost:9876 \
            -t $TOPIC \
            -p "$message_body" \
            -k "$message_key" \
            -g "$message_tag" >/dev/null 2>&1; then
            ((success_count++))
            print_success "消息 $i 发送成功 (Key: $message_key)"
        else
            ((failed_count++))
            print_error "消息 $i 发送失败"
        fi
        
        # 添加小延迟避免过快发送
        sleep 0.1
    done
    
    print_info "消息发送统计: 成功 $success_count 条, 失败 $failed_count 条"
    
    if [ $failed_count -eq 0 ]; then
        print_success "所有消息发送成功！"
        return 0
    else
        print_error "有 $failed_count 条消息发送失败"
        return 1
    fi
}

# 消费测试消息
consume_test_messages() {
    print_info "开始消费测试消息..."
    
    # 创建临时文件存储消费结果
    local consume_log="/tmp/rocketmq_consume_test.log"
    
    # 启动消费者（后台运行）
    docker exec -d $ROCKETMQ_CONTAINER sh mqadmin consumeMessage \
        -n localhost:9876 \
        -t $TOPIC \
        -g $CONSUMER_GROUP \
        -s true \
        -c $MESSAGE_COUNT > "$consume_log" 2>&1 &
    
    local consumer_pid=$!
    
    # 等待消费完成（最多等待30秒）
    local wait_count=0
    local max_wait=30
    
    while [ $wait_count -lt $max_wait ]; do
        if [ -f "$consume_log" ]; then
            local consumed_count=$(grep -c "ConsumeOK" "$consume_log" 2>/dev/null || echo "0")
            if [ $consumed_count -ge $MESSAGE_COUNT ]; then
                print_success "成功消费 $consumed_count 条消息"
                kill $consumer_pid 2>/dev/null || true
                rm -f "$consume_log"
                return 0
            fi
        fi
        
        sleep 1
        ((wait_count++))
        
        if [ $((wait_count % 5)) -eq 0 ]; then
            print_info "等待消费完成... (${wait_count}s)"
        fi
    done
    
    kill $consumer_pid 2>/dev/null || true
    print_error "消费测试超时"
    
    if [ -f "$consume_log" ]; then
        print_info "消费日志："
        cat "$consume_log"
        rm -f "$consume_log"
    fi
    
    return 1
}

# 查看 Topic 统计信息
show_topic_stats() {
    print_info "查看 Topic 统计信息..."
    
    echo -e "\n${BLUE}=== Topic Stats ===${NC}"
    docker exec $ROCKETMQ_CONTAINER sh mqadmin topicStatus -n localhost:9876 -t $TOPIC 2>/dev/null || {
        print_warning "无法获取 Topic 统计信息"
    }
    
    echo -e "\n${BLUE}=== Consumer Progress ===${NC}"
    docker exec $ROCKETMQ_CONTAINER sh mqadmin consumerProgress -n localhost:9876 -g $CONSUMER_GROUP 2>/dev/null || {
        print_warning "无法获取消费者进度信息"
    }
}

# 性能测试
performance_test() {
    print_info "开始性能测试..."
    
    local perf_topic="perf_test_topic"
    local message_size=1024  # 1KB
    local perf_message_count=1000
    
    # 创建性能测试 Topic
    docker exec $ROCKETMQ_CONTAINER sh mqadmin updateTopic \
        -n localhost:9876 \
        -t $perf_topic \
        -c DefaultCluster \
        -r 8 \
        -w 8 >/dev/null 2>&1
    
    print_info "性能测试参数: 消息大小=${message_size}B, 消息数量=${perf_message_count}"
    
    # 生产者性能测试
    echo -e "\n${BLUE}=== Producer Performance Test ===${NC}"
    docker exec $ROCKETMQ_CONTAINER sh mqadmin sendMessage \
        -n localhost:9876 \
        -t $perf_topic \
        -p "$(head -c $message_size </dev/zero | tr '\0' 'A')" \
        -c $perf_message_count 2>/dev/null || {
        print_warning "生产者性能测试失败"
    }
    
    # 清理性能测试 Topic
    docker exec $ROCKETMQ_CONTAINER sh mqadmin deleteTopic \
        -n localhost:9876 \
        -t $perf_topic \
        -c DefaultCluster >/dev/null 2>&1
}

# 清理测试数据
cleanup_test_data() {
    print_info "清理测试数据..."
    
    # 删除测试 Topic
    docker exec $ROCKETMQ_CONTAINER sh mqadmin deleteTopic \
        -n localhost:9876 \
        -t $TOPIC \
        -c DefaultCluster >/dev/null 2>&1 && {
        print_success "测试 Topic 删除成功"
    } || {
        print_warning "测试 Topic 删除失败，可能不存在"
    }
    
    # 删除消费者组
    docker exec $ROCKETMQ_CONTAINER sh mqadmin deleteSubGroup \
        -n localhost:9876 \
        -g $CONSUMER_GROUP \
        -c DefaultCluster >/dev/null 2>&1 && {
        print_success "测试消费者组删除成功"
    } || {
        print_warning "测试消费者组删除失败，可能不存在"
    }
}

# 显示系统信息
show_system_info() {
    echo -e "\n${BLUE}=== 系统信息 ===${NC}"
    echo "Docker 版本: $(docker --version)"
    echo "RocketMQ 容器状态:"
    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "(rocketmq|rocket)"
    
    echo -e "\n${BLUE}=== 内存使用情况 ===${NC}"
    docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}" | grep -E "(rocketmq|rocket)"
}

# 主函数
main() {
    echo -e "${GREEN}"
    echo "=================================================="
    echo "         RocketMQ 测试脚本 v1.0"
    echo "=================================================="
    echo -e "${NC}"
    
    print_info "开始 RocketMQ 功能测试..."
    
    # 显示系统信息
    show_system_info
    
    # 检查容器状态
    print_info "检查容器状态..."
    check_container_status $ROCKETMQ_CONTAINER || exit 1
    check_container_status $BROKER_CONTAINER || exit 1
    
    # 检查内存状态
    print_info "检查内存状态..."
    check_container_memory $ROCKETMQ_CONTAINER
    check_container_memory $BROKER_CONTAINER
    
    # 检查服务连通性
    check_rocketmq_connectivity || exit 1
    
    # 等待服务完全启动
    print_info "等待服务完全启动..."
    sleep 3
    
    # 创建测试 Topic
    create_test_topic
    
    # 发送测试消息
    if send_test_messages; then
        print_success "消息发送测试通过"
    else
        print_error "消息发送测试失败"
        exit 1
    fi
    
    # 等待消息投递
    print_info "等待消息投递..."
    sleep 2
    
    # 消费测试消息
    if consume_test_messages; then
        print_success "消息消费测试通过"
    else
        print_error "消息消费测试失败"
        # 不退出，继续执行其他测试
    fi
    
    # 显示统计信息
    show_topic_stats
    
    # 性能测试（可选）
    if [ "${1:-}" = "--performance" ] || [ "${1:-}" = "-p" ]; then
        performance_test
    fi
    
    # 清理测试数据（可选）
    if [ "${1:-}" != "--no-cleanup" ]; then
        print_info "5秒后将清理测试数据..."
        sleep 5
        cleanup_test_data
    fi
    
    echo -e "\n${GREEN}"
    echo "=================================================="
    echo "         RocketMQ 测试完成"
    echo "=================================================="
    echo -e "${NC}"
    
    print_success "测试脚本执行完成！"
}

# 帮助信息
show_help() {
    echo "RocketMQ 测试脚本使用说明:"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help          显示此帮助信息"
    echo "  -p, --performance   执行性能测试"
    echo "  --no-cleanup        不清理测试数据"
    echo ""
    echo "示例:"
    echo "  $0                  # 基本功能测试"
    echo "  $0 -p              # 包含性能测试"
    echo "  $0 --no-cleanup    # 不清理测试数据"
    echo ""
}

# 参数处理
case "${1:-}" in
    -h|--help)
        show_help
        exit 0
        ;;
    *)
        main "$@"
        ;;
esac

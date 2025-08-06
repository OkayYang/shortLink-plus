package cn.ywenrou.shortlink.system.mq.consumer;

import cn.ywenrou.shortlink.system.dto.req.LinkAccessStatsMessageDTO;
import cn.ywenrou.shortlink.system.service.LinkAccessStatsService;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 短链接访问统计消息消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
    topic = "${rocketmq.topic.link-access-stats:link_access_stats}",
    consumerGroup = "${rocketmq.consumer.group:link_access_stats_consumer_group}",
    selectorExpression = "${rocketmq.tag.link-access-stats:DEFAULT}"
)
public class LinkAccessStatsConsumer implements RocketMQListener<String> {

    private final LinkAccessStatsService linkAccessStatsService;

    /**
     * 消费短链接访问统计消息
     *
     * @param jsonMessage JSON格式的统计消息
     */
    @Override
    public void onMessage(String jsonMessage) {
        try {
            log.debug("收到短链接访问统计消息: {}", jsonMessage);
            
            // 将JSON字符串反序列化为对象
            LinkAccessStatsMessageDTO message = JSON.parseObject(jsonMessage, LinkAccessStatsMessageDTO.class);
            
            if (message == null) {
                log.error("消息反序列化失败，JSON: {}", jsonMessage);
                return;
            }
            
            log.debug("开始处理短链接访问统计消息: {}", message.getFullShortUrl());
            
            // 调用统计服务处理访问统计
            linkAccessStatsService.processLinkAccessStats(message);
            
            log.debug("短链接访问统计消息处理完成: {}", message.getFullShortUrl());
        } catch (Exception e) {
            log.error("处理短链接访问统计消息失败, JSON: {}, 错误信息: {}", 
                    jsonMessage, e.getMessage(), e);
            // 抛出异常让RocketMQ重试
            throw new RuntimeException("处理短链接访问统计消息失败，重试！", e);
        }
    }
} 
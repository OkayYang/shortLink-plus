package cn.ywenrou.shortlink.system.mq.producer;

import cn.ywenrou.shortlink.system.dto.req.LinkAccessStatsMessageDTO;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 短链接访问统计消息生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LinkAccessStatsProducer {

    private final RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.topic.link-access-stats:link_access_stats}")
    private String linkAccessStatsTopic;

    @Value("${rocketmq.tag.link-access-stats:DEFAULT}")
    private String linkAccessStatsTag;

    /**
     * 发送短链接访问统计消息
     *
     * @param message 统计消息
     */
    public void sendLinkAccessStatsMessage(LinkAccessStatsMessageDTO message) {
        try {
            String destination = linkAccessStatsTopic + ":" + linkAccessStatsTag;
            
            // 使用JSON序列化，确保消息能被正确发送
            String jsonMessage = JSON.toJSONString(message);
            
            rocketMQTemplate.asyncSend(destination, jsonMessage, null);
            log.debug("短链接访问统计消息发送成功: {}", message.getFullShortUrl());
        } catch (Exception e) {
            log.error("发送短链接访问统计消息异常: {}, 错误信息: {}", 
                    message.getFullShortUrl(), e.getMessage(), e);
            
            // 可以考虑添加重试机制或者降级处理
            handleSendFailure(message, e);
        }
    }

    /**
     * 处理发送失败的情况
     *
     * @param message 统计消息
     * @param e 异常
     */
    private void handleSendFailure(LinkAccessStatsMessageDTO message, Exception e) {
        // 可以在这里添加重试逻辑或者将消息存储到数据库等降级处理
        log.warn("消息发送失败，短链接: {}, 将进行降级处理", message.getFullShortUrl());
    }
} 
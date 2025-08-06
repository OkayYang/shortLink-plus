package cn.ywenrou.shortlink.system.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMQConfiguration {
    @Value("${rocketmq.name-server}")
    private String nameServer;
    @Value("${rocketmq.consumer.group}")
    private String producerGroup;

    @Bean
    public RocketMQTemplate rocketMqTemplate(){
        RocketMQTemplate rocketMqTemplate = new RocketMQTemplate();
        DefaultMQProducer producer = new DefaultMQProducer();
        producer.setNamesrvAddr(nameServer);
        producer.setProducerGroup(producerGroup);
        rocketMqTemplate.setProducer(producer);
        
        return rocketMqTemplate;
    }

}


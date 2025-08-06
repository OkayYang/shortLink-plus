package cn.ywenrou.shortlink.system.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 布隆过滤器配置
 * @author xuxiaoyang
 * @since 2025/6/28
 */
@Configuration(value = "rBloomFilterConfigurationBySystem")
public class RBloomFilterConfiguration {

    /**
     * 短链接生成检测布隆过滤器
     */
    @Bean
    public RBloomFilter<String> shortLinkCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("shortLinkCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(10000000L, 0.001);
        return cachePenetrationBloomFilter;
    }

}
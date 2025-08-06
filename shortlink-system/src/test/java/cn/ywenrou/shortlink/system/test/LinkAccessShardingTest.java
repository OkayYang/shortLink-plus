package cn.ywenrou.shortlink.system.test;

import cn.hutool.core.date.DateUtil;
import cn.ywenrou.shortlink.system.dao.entity.LinkAccessStatsDO;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * 短链接访问统计分片测试
 */
public class LinkAccessShardingTest {
    private static final String SQL = "CREATE TABLE `sl_link_access_stats_%d`\n" +
            "(\n" +
            "    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',\n" +
            "    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',\n" +
            "    `date`           date         DEFAULT NULL COMMENT '日期',\n" +
            "    `pv`             int(11) DEFAULT NULL COMMENT '访问量',\n" +
            "    `uv`             int(11) DEFAULT NULL COMMENT '独立访客数',\n" +
            "    `uip`            int(11) DEFAULT NULL COMMENT '独立IP数',\n" +
            "    `hour`           int(3) DEFAULT NULL COMMENT '小时',\n" +
            "    `weekday`        int(3) DEFAULT NULL COMMENT '星期',\n" +
            "    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',\n" +
            "    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',\n" +
            "    `del_time`        datetime DEFAULT NULL COMMENT '删除时间',\n" +
            "    `del_flag`       tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',\n" +
            "    PRIMARY KEY (`id`),\n" +
            "    UNIQUE KEY `idx_unique_access_stats` (`full_short_url`,`date`,`hour`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
    
    /**
     * 生成建表SQL
     */
    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf(SQL + "%n", i);
        }
    }
    
    /**
     * 测试分片算法
     */
    @Test
    public void testShardingAlgorithm() {
        String[] testUrls = {
            "https://example.com/abc",
            "https://example.com/def",
            "https://example.com/ghi",
            "https://example.com/jkl"
        };
        
        for (String url : testUrls) {
            int shardingValue = (url.hashCode() & Integer.MAX_VALUE) % 16;
            System.out.println("URL: " + url + ", Sharding Table: sl_link_access_stats_" + shardingValue);
        }
    }
    
    /**
     * 构建测试数据
     */
    @Test
    public void buildTestData() {
        Date now = new Date();
        
        // 构建测试数据
        LinkAccessStatsDO statsDO = LinkAccessStatsDO.builder()
                .fullShortUrl("https://example.com/test")
                .date(DateUtil.date())
                .pv(1)
                .uv(1)
                .uip(1)
                .hour(DateUtil.hour(now, true))
                .weekday(DateUtil.dayOfWeek(now) - 1)
                .build();
        
        System.out.println("Test Data: " + statsDO);
        
        // 计算分片表
        int shardingValue = (statsDO.getFullShortUrl().hashCode() & Integer.MAX_VALUE) % 16;
        System.out.println("Sharding Table: sl_link_access_stats_" + shardingValue);
    }
}

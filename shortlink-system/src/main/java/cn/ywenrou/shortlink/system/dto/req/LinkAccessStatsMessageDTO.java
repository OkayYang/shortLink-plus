package cn.ywenrou.shortlink.system.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 短链接访问统计消息DTO
 * 用于RocketMQ异步传递统计数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkAccessStatsMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 客户端IP地址
     */
    private String clientIp;

    /**
     * 用户代理字符串
     */
    private String userAgent;

    /**
     * 访问者ID（从Cookie获取或生成）
     */
    private String visitorId;

    /**
     * 是否为HTTPS请求
     */
    private Boolean isSecure;

    /**
     * 请求域名
     */
    private String domain;

    /**
     * 请求URI
     */
    private String uri;

    /**
     * 消息发送时间戳
     */
    private Long timestamp;

    /**
     * 消息发送日期
     */
    private Date date;

    /**
     * 消息发送小时
     */
    private int hour;
    /**
     * 消息发送第几天
     */
    private int weekday;


} 
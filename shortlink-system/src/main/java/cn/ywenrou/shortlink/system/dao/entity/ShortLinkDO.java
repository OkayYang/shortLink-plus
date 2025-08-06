package cn.ywenrou.shortlink.system.dao.entity;


import cn.ywenrou.shortlink.common.core.web.dao.BaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 短链接实体
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：link）获取项目资料
 */
@Data
@SuperBuilder
@TableName("sl_link")
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 创建者
     */
    private String username;

    /**
     * 启用标识 0：启用 1：未启用
     */
    private Integer enableStatus;

    /**
     * 创建类型 0：接口创建 1：控制台创建
     */
    private Integer createdType;


    /**
     * 有效期
     */
    private Date expireTime;

    /**
     * 描述
     */
    private String description;

    /**
     * 网站标识
     */
    private String favicon;

    /**
     * 历史PV
     */
    @TableField(exist = false)
    private Integer totalPv;

    /**
     * 历史UV
     */
    @TableField(exist = false)
    private Integer totalUv;

    /**
     * 历史UIP
     */
    @TableField(exist = false)
    private Integer totalUip;

    /**
     * 今日PV
     */
    @TableField(exist = false)
    private Integer todayPv;

    /**
     * 今日UV
     */
    @TableField(exist = false)
    private Integer todayUv;

    /**
     * 今日UIP
     */
    @TableField(exist = false)
    private Integer todayUip;

    /**
     * 点击量
     */
    @TableField(exist = false)
    private Integer clickNum;
}


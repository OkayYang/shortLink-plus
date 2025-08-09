package cn.ywenrou.shortlink.console.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 * 分组统计信息响应DTO
 * 包含分组基本信息和统计数据
 */
@Data
public class GroupStatsRespDTO {
    /**
     * 分组ID
     */
    private String gid;
    
    /**
     * 分组名称
     */
    private String name;
    
    /**
     * 分组描述
     */
    private String description;
    
    /**
     * 分组标签
     */
    private String tag;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 短链接数量
     */
    private Integer linkCount;
    
    /**
     * 总访问量(PV)
     */
    private Integer totalPv;
    
    /**
     * 总独立访客数(UV)
     */
    private Integer totalUv;
    
    /**
     * 总独立IP数(UIP)
     */
    private Integer totalUip;
}

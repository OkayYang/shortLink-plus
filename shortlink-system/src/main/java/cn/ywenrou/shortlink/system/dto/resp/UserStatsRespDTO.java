package cn.ywenrou.shortlink.system.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户统计信息响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsRespDTO {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 短链接总数
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
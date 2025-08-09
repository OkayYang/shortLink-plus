package cn.ywenrou.shortlink.system.dao.mapper;

import cn.ywenrou.shortlink.system.dao.entity.LinkAccessStatsDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {
    
    /**
     * 插入或更新访问统计数据
     * 如果记录存在则更新统计数据，不存在则插入新记录
     * 
     * @param linkAccessStats 访问统计数据对象
     * @return 影响的行数
     */
    int insertOrUpdate(LinkAccessStatsDO linkAccessStats);

    /**
     * 获取今日访问统计数据
     * 
     * @param fullShortUrl 完整短链接
     * @param today 今日日期，格式：yyyy-MM-dd
     * @return 包含pv、uv、uip的Map
     */
    Map<String, Object> getTodayStats(@Param("fullShortUrl") String fullShortUrl, @Param("today") String today);
    
    /**
     * 获取历史访问统计数据
     * 
     * @param fullShortUrl 完整短链接
     * @return 包含pv、uv、uip的Map
     */
    Map<String, Object> getTotalStats(@Param("fullShortUrl") String fullShortUrl);
    
    /**
     * 批量获取今日访问统计数据
     * 
     * @param fullShortUrls 完整短链接列表
     * @param today 今日日期，格式：yyyy-MM-dd
     * @return 包含fullShortUrl、pv、uv、uip的Map列表
     */
    List<Map<String, Object>> getBatchTodayStats(@Param("fullShortUrls") List<String> fullShortUrls, @Param("today") String today);
    
    /**
     * 批量获取历史访问统计数据
     * 
     * @param fullShortUrls 完整短链接列表
     * @return 包含fullShortUrl、pv、uv、uip的Map列表
     */
    List<Map<String, Object>> getBatchTotalStats(@Param("fullShortUrls") List<String> fullShortUrls);
    
    /**
     * 获取用户的所有短链接总访问统计数据
     * 
     * @param fullShortUrls 用户的短链接列表
     * @param username 用户名
     * @return 包含totalPv、totalUv、totalUip的Map
     */
    Map<String, Object> getUserTotalStats(@Param("fullShortUrls") List<String> fullShortUrls, @Param("username") String username);
    
    /**
     * 按分组聚合获取用户分组统计数据
     * 
     * @param username 用户名
     * @return 包含gid、linkCount、totalPv、totalUv、totalUip的Map列表
     */
    List<Map<String, Object>> getGroupStatsAggregation(@Param("username") String username);
}

package cn.ywenrou.shortlink.system.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.ywenrou.shortlink.common.redis.service.RedisService;
import cn.ywenrou.shortlink.system.common.utils.LinkUtil;
import cn.ywenrou.shortlink.system.dao.entity.LinkAccessStatsDO;
import cn.ywenrou.shortlink.system.dao.entity.ShortLinkDO;
import cn.ywenrou.shortlink.system.dao.mapper.LinkAccessStatsMapper;
import cn.ywenrou.shortlink.system.dao.mapper.ShortLinkMapper;
import cn.ywenrou.shortlink.system.dto.req.LinkAccessStatsMessageDTO;
import cn.ywenrou.shortlink.system.dto.resp.UserStatsRespDTO;
import cn.ywenrou.shortlink.system.service.LinkAccessStatsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Link access statistics service implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LinkAccessStatsServiceImpl extends ServiceImpl<LinkAccessStatsMapper, LinkAccessStatsDO> implements LinkAccessStatsService {

    private final RedisService redisService;
    private final ShortLinkMapper shortLinkMapper;
    private static final String UV_KEY_PREFIX = "shortlink:stats:uv:";
    private static final String UIP_KEY_PREFIX = "shortlink:stats:uip:";
    private static final String VISITOR_ID_COOKIE_NAME = "shortlink_visitor_id";
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 7; // 7天有效期

    
    @Override
    public void processLinkAccessStats(LinkAccessStatsMessageDTO message) {
        try {
            int hour = message.getHour();
            int weekday = message.getWeekday();
            Date now = message.getDate();



            // 创建基础统计对象
            LinkAccessStatsDO accessStats = LinkAccessStatsDO.builder()
                    .fullShortUrl(message.getFullShortUrl())
                    .hour(hour)
                    .weekday(weekday)
                    .date(now)
                    .pv(1)
                    .build();

            // 处理UV（独立访客）
            String uvKey = UV_KEY_PREFIX + message.getFullShortUrl();
            String uvVisitorKey = uvKey + ":" + message.getVisitorId();

            boolean isNewUv = !Boolean.TRUE.equals(redisService.hasKey(uvVisitorKey));
            if (isNewUv) {
                redisService.setCacheObject(uvVisitorKey, "1", DateUtil.between(now, DateUtil.endOfDay(now), DateUnit.SECOND), TimeUnit.SECONDS);
                accessStats.setUv(1);
            } else {
                accessStats.setUv(0);
            }

            // 处理UIP（独立IP）
            String uipKey = UIP_KEY_PREFIX + message.getFullShortUrl();
            String uipAddressKey = uipKey + ":" + message.getClientIp();

            boolean isNewIp = !Boolean.TRUE.equals(redisService.hasKey(uipAddressKey));
            if (isNewIp) {
                redisService.setCacheObject(uipAddressKey, "1", DateUtil.between(now, DateUtil.endOfDay(now), DateUnit.SECOND), TimeUnit.SECONDS);
                accessStats.setUip(1);
            } else {
                accessStats.setUip(0);
            }

            // 保存到数据库
            baseMapper.insertOrUpdate(accessStats);

        } catch (Exception e) {
            log.error("处理短链接访问统计数据出错 {}: {}", message.getFullShortUrl(), e.getMessage(), e);
        }
    }
    
    @Override
    public UserStatsRespDTO getUserStats(String username) {
        try {
            log.info("获取用户[{}]统计信息", username);
            
            // 1. 查询用户创建的所有短链接
            LambdaQueryWrapper<ShortLinkDO> linkQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getUsername, username)
                    .eq(ShortLinkDO::getDelFlag, 0);
            
            List<ShortLinkDO> shortLinks = shortLinkMapper.selectList(linkQueryWrapper);
            int linkCount = shortLinks.size();
            
            log.info("用户[{}]共有{}个短链接", username, linkCount);
            
            // 如果用户没有创建短链接，直接返回零值统计
            if (linkCount == 0) {
                return UserStatsRespDTO.builder()
                        .username(username)
                        .linkCount(0)
                        .totalPv(0)
                        .totalUv(0)
                        .totalUip(0)
                        .build();
            }
            
            // 2. 获取所有短链接的URL
            List<String> shortUrls = shortLinks.stream()
                    .map(ShortLinkDO::getFullShortUrl)
                    .toList();
            
            // 3. 查询这些短链接的总访问统计数据
            Map<String, Object> totalStats = null;
            try {
                totalStats = baseMapper.getUserTotalStats(shortUrls, username);
                log.info("用户[{}]统计数据: {}", username, totalStats);
            } catch (Exception e) {
                log.error("获取用户[{}]统计数据异常: {}", username, e.getMessage(), e);
            }
            
            // 4. 构建并返回统计结果
            return UserStatsRespDTO.builder()
                    .username(username)
                    .linkCount(linkCount)
                    .totalPv(totalStats != null ? ((Number) totalStats.getOrDefault("totalPv", 0)).intValue() : 0)
                    .totalUv(totalStats != null ? ((Number) totalStats.getOrDefault("totalUv", 0)).intValue() : 0)
                    .totalUip(totalStats != null ? ((Number) totalStats.getOrDefault("totalUip", 0)).intValue() : 0)
                    .build();
        } catch (Exception e) {
            log.error("获取用户[{}]统计信息异常: {}", username, e.getMessage(), e);
            return UserStatsRespDTO.builder()
                    .username(username)
                    .linkCount(0)
                    .totalPv(0)
                    .totalUv(0)
                    .totalUip(0)
                    .build();
        }
    }

    /**
     * 获取或创建访客ID
     * 如果请求中没有访客ID的cookie，则生成一个新的访客ID并设置到响应的cookie中
     */
    private String getOrCreateVisitorId(String fullShortUrl,HttpServletRequest request, HttpServletResponse response) {
        // 尝试从cookie中获取访客ID
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (VISITOR_ID_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 如果没有找到访客ID，则生成一个新的
        String visitorId = IdUtil.fastSimpleUUID();
        String domain = LinkUtil.getDomain(fullShortUrl);
        String path = LinkUtil.getUri(fullShortUrl);
        // 将访客ID设置到响应的cookie中
        Cookie visitorIdCookie = new Cookie(VISITOR_ID_COOKIE_NAME, visitorId);
        visitorIdCookie.setMaxAge(COOKIE_MAX_AGE);
        visitorIdCookie.setPath(path);
        visitorIdCookie.setDomain(domain);

        visitorIdCookie.setHttpOnly(true);

        // 如果是HTTPS请求，设置secure属性
        if (request.isSecure()) {
            visitorIdCookie.setSecure(true);
        }

        response.addCookie(visitorIdCookie);
        
        return visitorId;
    }
    
    @Override
    public List<Map<String, Object>> getGroupStatsAggregation(String username) {
        try {
            log.info("获取用户[{}]分组统计信息", username);
            
            // 1. 查询用户创建的所有短链接，按gid分组
            LambdaQueryWrapper<ShortLinkDO> linkQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getUsername, username)
                    .eq(ShortLinkDO::getDelFlag, 0);
            
            List<ShortLinkDO> shortLinks = shortLinkMapper.selectList(linkQueryWrapper);
            log.info("用户[{}]共有{}个短链接", username, shortLinks.size());
            
            if (shortLinks.isEmpty()) {
                return List.of();
            }
            
            // 2. 按gid分组，统计每个分组的链接数量
            Map<String, List<ShortLinkDO>> groupedLinks = shortLinks.stream()
                    .collect(Collectors.groupingBy(ShortLinkDO::getGid));
            
            // 3. 获取所有短链接的URL
            List<String> shortUrls = shortLinks.stream()
                    .map(ShortLinkDO::getFullShortUrl)
                    .collect(Collectors.toList());
            
            // 4. 查询这些短链接的访问统计数据
            List<Map<String, Object>> statsDataList = baseMapper.getGroupStatsAggregation(shortUrls);
            
            // 5. 将统计数据按URL建立映射
            Map<String, Map<String, Object>> statsMap = statsDataList.stream()
                    .collect(Collectors.toMap(
                            stats -> (String) stats.get("full_short_url"),
                            stats -> stats,
                            (v1, v2) -> v1
                    ));
            
            // 6. 为每个分组聚合统计数据
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map.Entry<String, List<ShortLinkDO>> entry : groupedLinks.entrySet()) {
                String gid = entry.getKey();
                List<ShortLinkDO> linksInGroup = entry.getValue();
                
                // 计算该分组的统计数据
                int linkCount = linksInGroup.size();
                long totalPv = 0;
                long totalUv = 0;
                long totalUip = 0;
                
                for (ShortLinkDO link : linksInGroup) {
                    Map<String, Object> linkStats = statsMap.get(link.getFullShortUrl());
                    if (linkStats != null) {
                        totalPv += ((Number) linkStats.getOrDefault("totalPv", 0)).longValue();
                        totalUv += ((Number) linkStats.getOrDefault("totalUv", 0)).longValue();
                        totalUip += ((Number) linkStats.getOrDefault("totalUip", 0)).longValue();
                    }
                }
                
                // 构建分组统计结果
                Map<String, Object> groupStats = new HashMap<>();
                groupStats.put("gid", gid);
                groupStats.put("linkCount", linkCount);
                groupStats.put("totalPv", totalPv);
                groupStats.put("totalUv", totalUv);
                groupStats.put("totalUip", totalUip);
                
                result.add(groupStats);
            }
            
            log.info("用户[{}]分组统计完成，共{}个分组", username, result.size());
            return result;
            
        } catch (Exception e) {
            log.error("获取用户[{}]分组统计信息异常: {}", username, e.getMessage(), e);
            return List.of(); // 返回空列表
        }
    }
} 
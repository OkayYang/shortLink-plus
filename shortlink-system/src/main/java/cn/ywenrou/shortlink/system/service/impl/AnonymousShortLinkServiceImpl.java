package cn.ywenrou.shortlink.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.ywenrou.shortlink.common.core.exception.ClientException;
import cn.ywenrou.shortlink.common.redis.service.RedisService;
import cn.ywenrou.shortlink.system.dao.entity.ShortLinkDO;
import cn.ywenrou.shortlink.system.dao.mapper.ShortLinkMapper;
import cn.ywenrou.shortlink.system.dto.req.GenerateShortLinkReqDTO;
import cn.ywenrou.shortlink.system.dto.req.ShortLinkCreateReqDTO;
import cn.ywenrou.shortlink.system.dto.resp.AnonymousHistoryRespDTO;
import cn.ywenrou.shortlink.system.dto.resp.ShortLinkCreateRespDTO;
import cn.ywenrou.shortlink.system.service.AnonymousShortLinkService;
import cn.ywenrou.shortlink.system.service.SystemShortLinkService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.ywenrou.shortlink.system.common.constant.RedisCacheConstants.*;

/**
 * 匿名用户短链接服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnonymousShortLinkServiceImpl implements AnonymousShortLinkService {
    
    private final SystemShortLinkService systemShortLinkService;
    private final RedisService redisService;
    private final ShortLinkMapper shortLinkMapper;
    private final RedissonClient redissonClient;
    
    @Value("${short-link.domain.default}")
    private String defaultDomain;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShortLinkCreateRespDTO generateShortLink(GenerateShortLinkReqDTO requestParam, HttpServletRequest request, HttpServletResponse response) {
        // 1. 获取或生成匿名用户标识
        String anonymousUserId = getOrCreateAnonymousUserId(request, response);
        
        // 2. 检查每日生成限制
        String today = DateUtil.today();
        String dailyCountKey = ANONYMOUS_USER_DAILY_COUNT_KEY + anonymousUserId + ":" + today;
        
        Integer dailyCount = redisService.getCacheObject(dailyCountKey);
        if (dailyCount != null && dailyCount >= ANONYMOUS_USER_DAILY_LIMIT) {
            throw new ClientException("每日生成短链接次数已达上限（" + ANONYMOUS_USER_DAILY_LIMIT + "次），请明日再试");
        }
        
        // 3. 构建短链接创建请求参数
        ShortLinkCreateReqDTO createReqDTO = ShortLinkCreateReqDTO.builder()
                .originUrl(requestParam.getOriginalUrl())
                .domain(defaultDomain)
                .gid(ANONYMOUS_USER_GROUP) // 匿名用户使用固定分组
                .type(0) // 接口创建
                .expireTime(DateUtil.offsetDay(new Date(), ANONYMOUS_HISTORY_DAYS)) // 默认7天有效期
                .describe("匿名用户生成")
                .username(anonymousUserId)
                .build();
        
        // 4. 调用短链接服务创建短链接
        ShortLinkCreateRespDTO result = systemShortLinkService.createShortLink(createReqDTO);
        String fullShortUrl = result.getFullShortUrl();
        
        // 5. 更新用户每日计数 - 计算到当天结束的时间差
        Date endOfDay = DateUtil.endOfDay(new Date());
        long timeToEndOfDay = DateUtil.between(new Date(), endOfDay, cn.hutool.core.date.DateUnit.SECOND);
        redisService.setCacheObject(dailyCountKey, (dailyCount == null ? 0 : dailyCount) + 1, timeToEndOfDay, TimeUnit.SECONDS);

        // 6. 异步更新用户历史记录缓存，优化性能
        String historyKey = ANONYMOUS_USER_HISTORY_KEY + anonymousUserId;
        // 清除用户历史记录缓存，下次查询时会重新从数据库加载
        redisService.deleteObject(historyKey);

        return ShortLinkCreateRespDTO.builder()
                .originUrl(createReqDTO.getOriginUrl())
                .fullShortUrl(fullShortUrl)
                .build();
    }
    
    @Override
    public List<AnonymousHistoryRespDTO> getAnonymousHistory(HttpServletRequest request) {
        // 1. 从Cookie中获取匿名用户ID
        String anonymousUserId = getAnonymousUserIdFromRequest(request);
        if (StrUtil.isBlank(anonymousUserId)) {
            return Collections.emptyList();
        }

        // 2. 尝试从Redis缓存获取历史记录
        String historyKey = ANONYMOUS_USER_HISTORY_KEY + anonymousUserId;
        List<AnonymousHistoryRespDTO> cachedHistoryList = redisService.getCacheList(historyKey);
        if (CollUtil.isNotEmpty(cachedHistoryList)) {
            log.debug("从缓存获取匿名用户[{}]历史记录，共{}条", anonymousUserId, cachedHistoryList.size());
            return cachedHistoryList;
        }
        // 3. 缓存未命中，从数据库查询
        RLock lock = redissonClient.getLock(ANONYMOUS_HISTORY_QUERY_LOCK+anonymousUserId);
        lock.lock();
        try {
            // 双重检查
            cachedHistoryList = redisService.getCacheList(historyKey);
            if (CollUtil.isNotEmpty(cachedHistoryList)) {
                log.debug("从缓存获取匿名用户[{}]历史记录，共{}条", anonymousUserId, cachedHistoryList.size());
                return cachedHistoryList;
            }
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getUsername, anonymousUserId)
                    .orderByDesc(ShortLinkDO::getCreateTime);

            List<ShortLinkDO> shortLinkList = shortLinkMapper.selectList(queryWrapper);
            
            if (CollUtil.isEmpty(shortLinkList)) {
                log.debug("匿名用户[{}]无历史记录", anonymousUserId);
                // 设置空缓存，避免缓存穿透
                redisService.setCacheList(historyKey, Collections.emptyList());
                redisService.expire(historyKey, ANONYMOUS_HISTORY_DAYS, TimeUnit.DAYS);
                return Collections.emptyList();
            }

            // 4. 转换为响应对象
            List<AnonymousHistoryRespDTO> historyList = shortLinkList.stream()
                    .map(shortLinkDO -> AnonymousHistoryRespDTO.builder()
                            .fullShortUrl(shortLinkDO.getFullShortUrl())
                            .originUrl(shortLinkDO.getOriginUrl())
                            .createTime(shortLinkDO.getCreateTime())
                            .expireTime(shortLinkDO.getExpireTime())
                            .description(shortLinkDO.getDescription())
                            .build())
                    .collect(Collectors.toList());

            // 5. 将结果缓存到Redis
            redisService.setCacheList(historyKey,historyList);
            redisService.expire(historyKey, ANONYMOUS_HISTORY_DAYS, TimeUnit.DAYS);
            return historyList;

        } catch (Exception e) {
            log.error("查询匿名用户[{}]历史记录失败: {}", anonymousUserId, e.getMessage(), e);
            return Collections.emptyList();
        }finally {
            lock.unlock();
        }
    }

    
    /**
     * 获取或创建匿名用户标识
     */
    private String getOrCreateAnonymousUserId(HttpServletRequest request, HttpServletResponse response) {
        // 1. 从Cookie中获取匿名用户ID
        String value = getAnonymousUserIdFromRequest(request);
        if (!StrUtil.isBlank(value)) {
            return value;
        }
        // 2. 如果没有找到，生成新的匿名用户ID
        String anonymousUserId = "anon_" + IdUtil.fastSimpleUUID();
        
        // 3. 设置Cookie
        Cookie cookie = new Cookie(ANONYMOUS_USER_COOKIE_KEY, anonymousUserId);
        cookie.setMaxAge(ANONYMOUS_HISTORY_SECONDS);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        
        return anonymousUserId;
    }

    /**
     * 从请求中获取匿名用户ID（不创建新的）
     */
    private String getAnonymousUserIdFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ANONYMOUS_USER_COOKIE_KEY.equals(cookie.getName()) && StrUtil.isNotBlank(cookie.getValue())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
} 
package cn.ywenrou.shortlink.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.ywenrou.shortlink.common.core.exception.ClientException;
import cn.ywenrou.shortlink.common.core.exception.ServiceException;
import cn.ywenrou.shortlink.common.core.utils.IpUtils;
import cn.ywenrou.shortlink.common.redis.service.RedisService;
import cn.ywenrou.shortlink.system.common.utils.HashUtil;
import cn.ywenrou.shortlink.system.common.utils.LinkUtil;
import cn.ywenrou.shortlink.system.dao.entity.ShortLinkDO;
import cn.ywenrou.shortlink.system.dao.entity.ShortLinkRouterDO;
import cn.ywenrou.shortlink.system.dao.mapper.LinkAccessStatsMapper;
import cn.ywenrou.shortlink.system.dao.mapper.ShortLinkMapper;
import cn.ywenrou.shortlink.system.dao.mapper.ShortLinkRouterMapper;
import cn.ywenrou.shortlink.system.dto.req.LinkAccessStatsMessageDTO;
import cn.ywenrou.shortlink.system.dto.req.ShortLinkCreateReqDTO;
import cn.ywenrou.shortlink.system.dto.req.ShortLinkDeleteReqDTO;
import cn.ywenrou.shortlink.system.dto.req.ShortLinkPageReqDTO;
import cn.ywenrou.shortlink.system.dto.req.ShortLinkUpdateReqDTO;
import cn.ywenrou.shortlink.system.dto.resp.ShortLinkCreateRespDTO;
import cn.ywenrou.shortlink.system.dto.resp.ShortLinkPageRespDTO;
import cn.ywenrou.shortlink.system.mq.producer.LinkAccessStatsProducer;
import cn.ywenrou.shortlink.system.service.LinkAccessStatsService;
import cn.ywenrou.shortlink.system.service.SystemShortLinkService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.ywenrou.shortlink.system.common.constant.RedisCacheConstants.SHORT_LINK_KEY_CACHE;
import static cn.ywenrou.shortlink.system.common.constant.RedisCacheConstants.SHORT_LINK_LOCK_REDIRECT;
import static cn.ywenrou.shortlink.system.common.constant.ShortLinkConstants.ERROR_PAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements SystemShortLinkService {
    private final RBloomFilter<String> shortLinkCachePenetrationBloomFilter;
    private final ShortLinkRouterMapper shortLinkRouterMapper;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final RedisService redisService;
    private final RedissonClient redissonClient;
    private final LinkAccessStatsService linkAccessStatsService;
    private final LinkAccessStatsProducer linkAccessStatsProducer;

    @Value("${shortlink.domain.default:s.ywenrou.cn}")
    private String defaultDomain;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        // 校验原始URL合法性
        validateUrl(requestParam.getOriginUrl());
        /**
         * 思考一个问题？这里也可能在分布式场景下大量数据库创建,导致生成的相同的短链接，插入数据，需要加锁吗？
         * 答案：不需要，首先我们通过uuid生成，我们先用布隆过滤器判断就算并发生成了一样插入，我们有数据库唯一索引兜底，
         * 如果采用分布式锁影响性能，我们只需温馨提示系统业务繁忙让用户重试就行了
         * 再如果大量布隆过滤器误判，那就扩容了
         */
        String username = requestParam.getUsername();
        String domain = "s.ywenrou.cn";
        String shortLinkSuffix = generateSuffix(domain, requestParam.getOriginUrl());

        String fullShortUrl = StrBuilder.create(domain)
                .append("/")
                .append(shortLinkSuffix)
                .toString();
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .username(username)
                .domain(domain)
                .originUrl(requestParam.getOriginUrl())
                .shortUri(shortLinkSuffix)
                .fullShortUrl(fullShortUrl)
                .gid(requestParam.getGid())
                .createdType(requestParam.getType())
                .expireTime(requestParam.getExpireTime())
                .description(requestParam.getDescribe())
                .build();

        try {
            baseMapper.insert(shortLinkDO);
            shortLinkRouterMapper.insert(ShortLinkRouterDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .gid(requestParam.getGid())
                    .build());

            shortLinkCachePenetrationBloomFilter.add(shortLinkSuffix);
        } catch (DuplicateKeyException ex) {
            /**
             * 1.并发处理：当多个用户同时请求创建相同短链接时，可能会发生并发插入数据库的情况。
             * 由于数据库表中对短链接URI设置了唯一索引约束，当第二个线程尝试插入相同的短链接时，会抛出主键或唯一索引冲突异常。
             * 2. 布隆过滤器更新：即使插入数据库失败，代码仍然确保将这个短链接添加到布隆过滤器中，这样做的目的是：
             * 防止缓存穿透：即使数据库插入失败，也要确保布隆过滤器知道这个短链接"存在"
             * 保持一致性：确保布隆过滤器和数据库的数据一致
             */
            shortLinkCachePenetrationBloomFilter.add(fullShortUrl);
            throw new ServiceException("当前服务繁忙，请稍后再试");
        }
        //缓存预热
        redisService.setCacheObject(SHORT_LINK_KEY_CACHE + fullShortUrl, requestParam.getOriginUrl(),
                LinkUtil.getLinkCacheExpireTime(requestParam.getExpireTime()), TimeUnit.MINUTES);
        //将已经使用的短链加入布隆过滤器
        shortLinkCachePenetrationBloomFilter.add(fullShortUrl);

        return ShortLinkCreateRespDTO.builder()
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .fullShortUrl(fullShortUrl)
                .build();
    }

    @Override
    public void deleteShortLink(ShortLinkDeleteReqDTO requestParam) {
        String username = requestParam.getUsername();
        // 先看布隆过滤器是否存在
        if (!shortLinkCachePenetrationBloomFilter.contains(requestParam.getShortUrl())) {
            throw new ClientException("短链接不存在");
        }
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .set(ShortLinkDO::getDelFlag, 1)
                .set(ShortLinkDO::getDelTime, new Date())
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getShortUrl())
                .eq(ShortLinkDO::getUsername, username);
        baseMapper.update(updateWrapper);
        //缓存删除
        redisService.deleteObject(SHORT_LINK_KEY_CACHE + requestParam.getShortUrl());
    }

    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        //查询短链接是否存在,因为需要判断是否可用，因此缓存不行，布隆过滤器没法删除
        String username = requestParam.getUsername();
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getShortUrl())
                .eq(ShortLinkDO::getUsername, username);

        ShortLinkDO existShortLinkDO = baseMapper.selectOne(queryWrapper);
        if (existShortLinkDO == null) {
            throw new ClientException("短链接不存在");
        }

        //进行更新
        baseMapper.update(ShortLinkDO.builder()
                .originUrl(requestParam.getOriginUrl())
                .expireTime(requestParam.getExpireTime())
                .description(requestParam.getDescribe())
                .enableStatus(requestParam.getEnableStatus())
                .build(), queryWrapper);
        
        //删除缓存，保障数据的一致性
        if (!Objects.equals(existShortLinkDO.getExpireTime(), requestParam.getExpireTime())
                || !Objects.equals(existShortLinkDO.getOriginUrl(), requestParam.getOriginUrl())) {
            redisService.deleteObject(SHORT_LINK_KEY_CACHE + requestParam.getShortUrl());
        }
    }

    @Override
    public IPage<ShortLinkPageRespDTO> listShortLink(ShortLinkPageReqDTO requestParam) {
        // 获取当前用户名
        String username = requestParam.getUsername();
        IPage<ShortLinkDO> page = new Page<>(requestParam.getCurrent(), requestParam.getSize());
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getUsername, username)
                .orderByDesc(ShortLinkDO::getCreateTime);

        // 执行分页查询
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(page, queryWrapper);
        
        // 如果没有数据，直接返回空结果
        if (resultPage.getRecords().isEmpty()) {
            return resultPage.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
        }
        
        // 获取所有短链接URL
        List<String> shortUrls = resultPage.getRecords().stream()
                .map(ShortLinkDO::getFullShortUrl)
                .collect(Collectors.toList());
        
        // 获取今日日期
        String today = DateUtil.formatDate(new Date());
        
        // 批量获取今日访问统计数据
        List<Map<String, Object>> todayStatsList = linkAccessStatsMapper.getBatchTodayStats(shortUrls, today);
        Map<String, Map<String, Object>> todayStatsMap = todayStatsList.stream()
                .collect(Collectors.toMap(
                        map -> (String) map.get("full_short_url"),
                        map -> map,
                        (v1, v2) -> v1
                ));
        
        // 批量获取历史访问统计数据
        List<Map<String, Object>> totalStatsList = linkAccessStatsMapper.getBatchTotalStats(shortUrls);
        Map<String, Map<String, Object>> totalStatsMap = totalStatsList.stream()
                .collect(Collectors.toMap(
                        map -> (String) map.get("full_short_url"),
                        map -> map,
                        (v1, v2) -> v1
                ));
        
        // 转换结果，填充访问统计数据
        return resultPage.convert(each -> {
            ShortLinkPageRespDTO respDTO = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            
            // 填充今日访问统计数据
            Map<String, Object> todayStats = todayStatsMap.get(each.getFullShortUrl());
            if (todayStats != null) {
                respDTO.setTodayPv(((Number) todayStats.getOrDefault("todayPv", 0)).intValue());
                respDTO.setTodayUv(((Number) todayStats.getOrDefault("todayUv", 0)).intValue());
                respDTO.setTodayUip(((Number) todayStats.getOrDefault("todayUip", 0)).intValue());
            } else {
                respDTO.setTodayPv(0);
                respDTO.setTodayUv(0);
                respDTO.setTodayUip(0);
            }
            
            // 填充历史访问统计数据
            Map<String, Object> totalStats = totalStatsMap.get(each.getFullShortUrl());
            if (totalStats != null) {
                respDTO.setTotalPv(((Number) totalStats.getOrDefault("totalPv", 0)).intValue());
                respDTO.setTotalUv(((Number) totalStats.getOrDefault("totalUv", 0)).intValue());
                respDTO.setTotalUip(((Number) totalStats.getOrDefault("totalUip", 0)).intValue());
            } else {
                respDTO.setTotalPv(0);
                respDTO.setTotalUv(0);
                respDTO.setTotalUip(0);
            }
            
            return respDTO;
        });
    }

    @Override
    public void redirectShortLink(String shortUri, HttpServletRequest request, HttpServletResponse response) {

        // 1. 从缓存中查询原始链接
        String fullShortUrl = StrBuilder.create(request.getServerName())
                .append("/")
                .append(shortUri)
                .toString();

        // 异步发送访问统计消息
        sendLinkAccessStatsMessage(fullShortUrl, request, response);
        
        String originalUrl = redisService.getCacheObject(SHORT_LINK_KEY_CACHE + fullShortUrl);
        if (StrUtil.isNotBlank(originalUrl)) {
            setRedirectResponse(originalUrl, response);
            return;
        }
        // 2. 使用布隆过滤器快速判断是否存在短链接
        if (!shortLinkCachePenetrationBloomFilter.contains(fullShortUrl)) {
            setRedirectResponse(ERROR_PAGE, response);
            return;
        }
        // 3. 采用分布式锁访问数据库，防止缓存击穿
        RLock lock = redissonClient.getLock(SHORT_LINK_LOCK_REDIRECT + fullShortUrl);
        lock.lock();
        try {
            // 双重检查,因为分布式场景下可能已经缓存
            originalUrl = redisService.getCacheObject(SHORT_LINK_KEY_CACHE + fullShortUrl);
            if (StrUtil.isNotBlank(originalUrl)) {
                setRedirectResponse(originalUrl, response);
                return;
            }
            // 先从路由表找到gid分片键
            ShortLinkRouterDO shortLinkRouterDO = shortLinkRouterMapper.selectOne(Wrappers.lambdaQuery(ShortLinkRouterDO.class)
                    .eq(ShortLinkRouterDO::getFullShortUrl, fullShortUrl));
            if (shortLinkRouterDO == null) {
                setRedirectResponse(ERROR_PAGE, response);
                return;
            }
            // 从数据库中查询原始链接
            ShortLinkDO shortLinkDO = baseMapper.selectOne(Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkRouterDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getEnableStatus, 1));

            // 数据不存在或者过期时间小于当前时间
            if (shortLinkDO == null || shortLinkDO.getExpireTime().before(new Date())) {
                setRedirectResponse(ERROR_PAGE, response);
                return;
            }
            originalUrl = shortLinkDO.getOriginUrl();
            // 缓存预热
            redisService.setCacheObject(SHORT_LINK_KEY_CACHE + fullShortUrl, originalUrl,
                    LinkUtil.getLinkCacheExpireTime(shortLinkDO.getExpireTime()), TimeUnit.MINUTES);
            //完成跳转
            setRedirectResponse(originalUrl, response);
        } finally {
            lock.unlock();
        }
    }

    private String generateSuffix(String domain, String originUrl) {
        //一个长链接可以对应多个短链接
        String salt = IdUtil.randomUUID();
        String result = originUrl + salt;
        String shorUri = HashUtil.hashToBase62(result);
        String fullShortUrl = StrBuilder.create(domain)
                .append("/")
                .append(shorUri)
                .toString();
        if (shortLinkCachePenetrationBloomFilter.contains(fullShortUrl)) {
            throw new ServiceException("当前服务繁忙，请稍后再试");
        }
        return shorUri;
    }
    
    private void setRedirectResponse(String originalUrl, ServletResponse response) {
        try {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.sendRedirect(originalUrl);
        } catch (IOException e) {
            throw new ServiceException("跳转异常");
        }
    }

    /**
     * 校验URL合法性，不合法直接抛出异常
     */
    private void validateUrl(String url) {
        try {
            new URL(url);
        } catch (Exception e) {
            throw new ClientException("原始链接不合法，请输入正确的URL");
        }
    }

    /**
     * 发送短链接访问统计消息到RocketMQ
     */
    private void sendLinkAccessStatsMessage(String fullShortUrl, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 获取或创建访客ID（模拟原有逻辑）
            String visitorId = getOrCreateVisitorId(fullShortUrl, request, response);
            // 获取当前日期
            long timestamp = System.currentTimeMillis();
            Date now = new Date(timestamp);
            int hour = DateUtil.hour(now, true);
            int weekday = DateUtil.dayOfWeek(now);
            // 构建消息
            LinkAccessStatsMessageDTO message = LinkAccessStatsMessageDTO.builder()
                    .fullShortUrl(fullShortUrl)
                    .clientIp(IpUtils.getIpAddr(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .visitorId(visitorId)
                    .isSecure(request.isSecure())
                    .domain(LinkUtil.getDomain("https://"+fullShortUrl))
                    .uri(LinkUtil.getUri("https://"+fullShortUrl))
                    .timestamp(timestamp)
                    .date(now)
                    .hour(hour)
                    .weekday(weekday)
                    .build();
            
            // 异步发送消息
            linkAccessStatsProducer.sendLinkAccessStatsMessage(message);
        } catch (Exception e) {
            log.error("发送短链接访问统计消息失败: {}, 错误信息: {}", fullShortUrl, e.getMessage(), e);
        }
    }

    /**
     * 获取或创建访客ID（简化版本，从原有服务中提取）
     */
    private String getOrCreateVisitorId(String fullShortUrl, HttpServletRequest request, HttpServletResponse response) {
        // 尝试从cookie中获取访客ID
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("shortlink_visitor_id".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        // 如果没有找到访客ID，则生成一个新的
        return IdUtil.fastSimpleUUID();
    }
}

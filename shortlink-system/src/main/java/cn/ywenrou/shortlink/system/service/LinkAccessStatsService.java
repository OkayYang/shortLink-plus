package cn.ywenrou.shortlink.system.service;

import cn.ywenrou.shortlink.system.dao.entity.LinkAccessStatsDO;
import cn.ywenrou.shortlink.system.dto.req.LinkAccessStatsMessageDTO;
import cn.ywenrou.shortlink.system.dto.resp.UserStatsRespDTO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * Link access statistics service
 */
public interface LinkAccessStatsService extends IService<LinkAccessStatsDO> {

    /**
     * Process link access statistics from message queue
     * @param message Link access statistics message
     */
    void processLinkAccessStats(LinkAccessStatsMessageDTO message);
    
    /**
     * Get user statistics information
     * @param username Username
     * @return User statistics data
     */
    UserStatsRespDTO getUserStats(String username);
} 
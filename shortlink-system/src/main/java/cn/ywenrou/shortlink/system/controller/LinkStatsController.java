package cn.ywenrou.shortlink.system.controller;

import cn.ywenrou.shortlink.common.core.domain.AjaxResult;
import cn.ywenrou.shortlink.common.core.web.controller.BaseController;
import cn.ywenrou.shortlink.system.service.LinkAccessStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接访问统计控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/short-link")
public class LinkStatsController extends BaseController {

    private final LinkAccessStatsService linkAccessStatsService;

    /**
     * 查看数据统计信息
     */
    @GetMapping("/stats/user")
    public AjaxResult getUserStats(@RequestParam("username") String username) {
        return success(linkAccessStatsService.getUserStats(username));
    }
    
    /**
     * 获取用户分组统计聚合数据
     */
    @GetMapping("/stats/groups")
    public AjaxResult getGroupStatsAggregation(@RequestParam("username") String username) {
        return success(linkAccessStatsService.getGroupStatsAggregation(username));
    }
} 
package cn.ywenrou.shortlink.console.controller;

import cn.ywenrou.shortlink.common.core.domain.AjaxResult;
import cn.ywenrou.shortlink.common.core.exception.ClientException;
import cn.ywenrou.shortlink.common.core.web.controller.BaseController;
import cn.ywenrou.shortlink.common.security.utils.SecurityUtils;
import cn.ywenrou.shortlink.console.dto.req.ShortLinkCreateReqDTO;
import cn.ywenrou.shortlink.console.dto.req.ShortLinkDeleteReqDTO;
import cn.ywenrou.shortlink.console.dto.req.ShortLinkUpdateReqDTO;
import cn.ywenrou.shortlink.console.remote.RemoteSystemService;
import cn.ywenrou.shortlink.console.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/link")
public class ShortLinkController extends BaseController {
    private final RemoteSystemService remoteSystemService;
    private final GroupService groupService;

    @PostMapping("/create")
    public AjaxResult createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        // 设置用户名
        String username = SecurityUtils.getUsername();
        requestParam.setUsername(username);
        
        // 处理gid为null的情况，使用默认分组
        if (requestParam.getGid() == null || requestParam.getGid().isEmpty()) {
            requestParam.setGid(groupService.getDefaultGroup());
        } else {
            // 验证gid是否属于当前用户
            if (!groupService.validateGroup(requestParam.getGid())) {
                throw new ClientException("分组不存在或不属于当前用户");
            }
        }
        
        return remoteSystemService.createShortLink(requestParam);
    }

    @DeleteMapping("/delete")
    public AjaxResult deleteShortLink(@RequestBody ShortLinkDeleteReqDTO requestParam) {
        // 设置用户名
        String username = SecurityUtils.getUsername();
        requestParam.setUsername(username);
        
        // 验证gid是否属于当前用户
        if (!groupService.validateGroup(requestParam.getGid())) {
            throw new ClientException("分组不存在或不属于当前用户");
        }
        
        return remoteSystemService.deleteShortLink(requestParam);
    }

    @PutMapping("/update")
    public AjaxResult updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        // 设置用户名
        String username = SecurityUtils.getUsername();
        requestParam.setUsername(username);
        
        // 验证gid是否属于当前用户
        if (!groupService.validateGroup(requestParam.getGid())) {
            throw new ClientException("分组不存在或不属于当前用户");
        }
        
        return remoteSystemService.updateShortLink(requestParam);
    }

    @GetMapping("/list")
    public AjaxResult listShortLink(@RequestParam("gid") String gid,
                                   @RequestParam("current") Long current,
                                   @RequestParam("size") Long size) {
        // 设置用户名
        String username = SecurityUtils.getUsername();
        
        // 验证gid是否属于当前用户
        if (!groupService.validateGroup(gid)) {
            throw new ClientException("分组不存在或不属于当前用户");
        }
        
        return remoteSystemService.listShortLink(gid, current, size, username);
    }
}

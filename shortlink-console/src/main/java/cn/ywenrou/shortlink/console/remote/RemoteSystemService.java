package cn.ywenrou.shortlink.console.remote;

import cn.ywenrou.shortlink.common.core.domain.AjaxResult;
import cn.ywenrou.shortlink.console.dto.req.ShortLinkCreateReqDTO;
import cn.ywenrou.shortlink.console.dto.req.ShortLinkDeleteReqDTO;
import cn.ywenrou.shortlink.console.dto.req.ShortLinkUpdateReqDTO;
import cn.ywenrou.shortlink.console.remote.factory.RemoteSystemFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "remoteSystemService",value = "shortlink-system",fallbackFactory = RemoteSystemFallbackFactory.class )
public interface RemoteSystemService {
    @PostMapping("/short-link/create")
    public AjaxResult createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam);

    @DeleteMapping("/short-link/delete")
    public AjaxResult deleteShortLink(@RequestBody ShortLinkDeleteReqDTO requestParam);
    
    @PutMapping("/short-link/update")
    public AjaxResult updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam);
    
    @GetMapping("/short-link/list")
    public AjaxResult listShortLink(@RequestParam("gid") String gid,
                                    @RequestParam("current") Long current,
                                    @RequestParam("size") Long size,
                                    @RequestParam("username") String username);
    
    /**
     * 获取用户短链接统计信息
     */
    @GetMapping("/short-link/stats/user")
    public AjaxResult getUserStats(@RequestParam("username") String username);
}

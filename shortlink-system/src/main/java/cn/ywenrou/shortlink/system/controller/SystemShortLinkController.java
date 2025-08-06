package cn.ywenrou.shortlink.system.controller;

import cn.ywenrou.shortlink.common.core.domain.AjaxResult;
import cn.ywenrou.shortlink.common.core.web.controller.BaseController;
import cn.ywenrou.shortlink.system.dto.req.ShortLinkCreateReqDTO;
import cn.ywenrou.shortlink.system.dto.req.ShortLinkDeleteReqDTO;
import cn.ywenrou.shortlink.system.dto.req.ShortLinkPageReqDTO;
import cn.ywenrou.shortlink.system.dto.req.ShortLinkUpdateReqDTO;
import cn.ywenrou.shortlink.system.service.SystemShortLinkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/short-link")
public class SystemShortLinkController extends BaseController {
    private final SystemShortLinkService shortLinkService;

    @PostMapping("/create")
    public AjaxResult createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return success(shortLinkService.createShortLink(requestParam));
    }

    @DeleteMapping("/delete")
    public AjaxResult deleteShortLink(@RequestBody ShortLinkDeleteReqDTO requestParam) {
        shortLinkService.deleteShortLink(requestParam);
        return success();
    }
    
    @PutMapping("/update")
    public AjaxResult updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkService.updateShortLink(requestParam);
        return success();
    }
    
    @GetMapping("/list")
    public AjaxResult listShortLink(@RequestParam("gid") String gid,
                                    @RequestParam("current") Long current,
                                    @RequestParam("size") Long size,
                                    @RequestParam("username") String username) {
        ShortLinkPageReqDTO requestParam = ShortLinkPageReqDTO.builder()
                .gid(gid)
                .current(current)
                .size(size)
                .username(username)
                .build();
        return success(shortLinkService.listShortLink(requestParam));
    }
    
    @GetMapping("/redirect/{short-uri}")
    public void redirectShortLink(@PathVariable("short-uri") String shortUri, HttpServletRequest request, HttpServletResponse response) {
        shortLinkService.redirectShortLink(shortUri, request, response);
    }
}

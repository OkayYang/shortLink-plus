package cn.ywenrou.shortlink.system.controller;

import cn.ywenrou.shortlink.common.core.domain.AjaxResult;
import cn.ywenrou.shortlink.common.core.web.controller.BaseController;
import cn.ywenrou.shortlink.system.dto.req.GenerateShortLinkReqDTO;
import cn.ywenrou.shortlink.system.service.AnonymousShortLinkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 匿名用户短链接Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/short-link")
public class AnonymousShortLinkController extends BaseController {
    
    private final AnonymousShortLinkService anonymousShortLinkService;

    /**
     * 游客生成短链接
     * @param requestParam 请求参数
     * @param request HTTP请求
     * @param response HTTP响应
     * @return 生成结果
     */
    @PostMapping("/generate")
    public AjaxResult generateShortLink(@RequestBody GenerateShortLinkReqDTO requestParam, 
                                        HttpServletRequest request, 
                                        HttpServletResponse response) {
        return success(anonymousShortLinkService.generateShortLink(requestParam, request, response));
    }

    /**
     * 游客查询历史记录
     * @param request HTTP请求
     * @return 历史记录列表
     */
    @GetMapping("/history")
    public AjaxResult getAnonymousHistory(HttpServletRequest request) {
        return success(anonymousShortLinkService.getAnonymousHistory(request));
    }
} 
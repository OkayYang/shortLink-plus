package cn.ywenrou.shortlink.system.service;

import cn.ywenrou.shortlink.system.dto.req.GenerateShortLinkReqDTO;
import cn.ywenrou.shortlink.system.dto.resp.AnonymousHistoryRespDTO;
import cn.ywenrou.shortlink.system.dto.resp.ShortLinkCreateRespDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 匿名用户短链接服务
 */
public interface AnonymousShortLinkService {
    
    /**
     * 匿名生成短链接
     * @param requestParam 请求参数
     * @param request HTTP请求
     * @param response HTTP响应
     * @return 短链接创建响应
     */
    ShortLinkCreateRespDTO generateShortLink(GenerateShortLinkReqDTO requestParam, HttpServletRequest request, HttpServletResponse response);
    
    /**
     * 查询匿名用户历史记录
     * @param request HTTP请求
     * @return 历史记录列表
     */
    List<AnonymousHistoryRespDTO> getAnonymousHistory(HttpServletRequest request);
} 
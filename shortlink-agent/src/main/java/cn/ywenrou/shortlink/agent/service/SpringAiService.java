package cn.ywenrou.shortlink.agent.service;

import cn.ywenrou.shortlink.agent.dto.req.ShortLinkChatReqDTO;
import cn.ywenrou.shortlink.agent.dto.resp.ShortLinkChatStreamRespDTO;
import reactor.core.publisher.Flux;

/**
 * Spring AI 服务接口
 */
public interface SpringAiService {
    

    /**
     * 发送聊天消息，返回流式响应（带模型参数）
     * @param requestParams 请求参数
     * @return 流式AI响应
     */
    Flux<ShortLinkChatStreamRespDTO> generateShortLinkContext(ShortLinkChatReqDTO requestParams);
}

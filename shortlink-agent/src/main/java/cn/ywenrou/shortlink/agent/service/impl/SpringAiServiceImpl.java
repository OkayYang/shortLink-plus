package cn.ywenrou.shortlink.agent.service.impl;

import cn.ywenrou.shortlink.agent.dto.req.ShortLinkChatReqDTO;
import cn.ywenrou.shortlink.agent.dto.resp.ShortLinkChatStreamRespDTO;
import cn.ywenrou.shortlink.agent.factory.OpenAiChatModelFactory;
import cn.ywenrou.shortlink.agent.service.SpringAiService;
import cn.ywenrou.shortlink.agent.tools.ShortLinkTools;
import cn.ywenrou.shortlink.agent.utils.StreamResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpringAiServiceImpl implements SpringAiService {
    private final ShortLinkTools shortLinkTools;
    private final OpenAiChatModelFactory openAiChatModelFactory;


    @Override
    public Flux<ShortLinkChatStreamRespDTO> generateShortLinkContext(ShortLinkChatReqDTO requestParams) {
        String message = requestParams.getMessage();
        String model = requestParams.getModel();
        OpenAiChatModel openAiChatModel = openAiChatModelFactory.createModel(model);
        String requestId = UUID.randomUUID().toString();
        log.info("开始处理聊天请求，ID: {}, 消息: {}", requestId, message);
        
        return Flux.defer(() -> {
            try {
                // 创建ChatClient并注册工具
                ChatClient chatClient = ChatClient.create(openAiChatModel);

                // 检查是否需要工具调用
                if (needsToolCalling(message)) {
                    return handleWithTools(chatClient, message, model, requestId);
                } else {
                    return handleWithoutTools(chatClient, message, model, requestId);
                }

            } catch (Exception e) {
                log.error("处理聊天请求失败", e);
                return Flux.just(StreamResponseUtils.createErrorResponse(model, requestId, e.getMessage()));
            }
        });
    }
    
    /**
     * 检查是否需要工具调用
     */
    private boolean needsToolCalling(String message) {
        // 检查是否包含URL
        String urlPattern = "(https?://[\\w\\-./?=&%#]+)";
        return message.matches(".*" + urlPattern + ".*") || 
               message.contains("链接") || 
               message.contains("网址") ||
               message.contains("网站");
    }
    
    /**
     * 使用工具处理
     */
    private Flux<ShortLinkChatStreamRespDTO> handleWithTools(ChatClient chatClient, String message, String model, String requestId) {
        return chatClient.prompt(message)
                .tools(shortLinkTools)
                .stream()
                .chatResponse()
                .doOnNext(response -> StreamResponseUtils.logResponseStats(response, requestId))
                .filter(StreamResponseUtils::isValidResponse)
                .map(response -> {
                    String content = response.getResult().getOutput().getText();
                    return StreamResponseUtils.createSafeStreamResponse(content, model, requestId);
                })
                .concatWith(Mono.just(ShortLinkChatStreamRespDTO.createFinishResponse(model, requestId)))
                .onErrorResume(e -> {
                    log.error("工具调用处理失败", e);
                    return Flux.just(StreamResponseUtils.createErrorResponse(model, requestId, e.getMessage()));
                });
    }
    
    /**
     * 不使用工具处理
     */
    private Flux<ShortLinkChatStreamRespDTO> handleWithoutTools(ChatClient chatClient, String message, String model, String requestId) {
        return chatClient.prompt(message)
                .stream()
                .chatResponse()
                .doOnNext(response -> StreamResponseUtils.logResponseStats(response, requestId))
                .filter(StreamResponseUtils::isValidResponse)
                .map(response -> {
                    String content = response.getResult().getOutput().getText();
                    return StreamResponseUtils.createSafeStreamResponse(content, model, requestId);
                })
                .concatWith(Mono.just(ShortLinkChatStreamRespDTO.createFinishResponse(model, requestId)))
                .onErrorResume(e -> {
                    log.error("普通聊天处理失败", e);
                    return Flux.just(StreamResponseUtils.createErrorResponse(model, requestId, e.getMessage()));
                });
    }
}

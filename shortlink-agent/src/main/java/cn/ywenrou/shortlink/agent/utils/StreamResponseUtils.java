package cn.ywenrou.shortlink.agent.utils;

import cn.ywenrou.shortlink.agent.dto.resp.ShortLinkChatStreamRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 流式响应处理工具类
 * 提供安全的响应处理方法，避免空指针异常
 */
@Slf4j
public class StreamResponseUtils {



    /**
     * 过滤有效的ChatResponse
     * @param response ChatResponse对象
     * @return 是否有效
     */
    public static boolean isValidResponse(ChatResponse response) {
        return response != null && 
               response.getResult() != null && 
               response.getResult().getOutput() != null;
    }

    /**
     * 创建流式响应，包含空值检查
     * @param content 内容
     * @param model 模型名称
     * @param requestId 请求ID
     * @return 流式响应对象
     */
    public static ShortLinkChatStreamRespDTO createSafeStreamResponse(String content, String model, String requestId) {
        return ShortLinkChatStreamRespDTO.createStreamResponse(
                content != null ? content : "", 
                model != null ? model : "gpt-3.5-turbo", 
                requestId != null ? requestId : UUID.randomUUID().toString()
        );
    }

    /**
     * 处理流式响应，自动过滤无效响应
     * @param responseFlux 原始响应流
     * @param model 模型名称
     * @param requestId 请求ID
     * @return 处理后的流式响应
     */
    public static Flux<ShortLinkChatStreamRespDTO> processStreamResponse(
            Flux<ChatResponse> responseFlux, 
            String model, 
            String requestId) {
        
        return responseFlux
                .filter(StreamResponseUtils::isValidResponse)
                .map(response -> {
                    String content = response.getResult().getOutput().getText();
                    return createSafeStreamResponse(content, model, requestId);
                })
                .concatWith(Mono.just(ShortLinkChatStreamRespDTO.createFinishResponse(model, requestId)))
                .onErrorResume(e -> {
                    log.error("处理流式响应时发生错误", e);
                    return Flux.just(createErrorResponse(model, requestId, e.getMessage()));
                });
    }

    /**
     * 创建错误响应
     * @param model 模型名称
     * @param requestId 请求ID
     * @param errorMessage 错误消息
     * @return 错误响应对象
     */
    public static ShortLinkChatStreamRespDTO createErrorResponse(String model, String requestId, String errorMessage) {
        return ShortLinkChatStreamRespDTO.builder()
                .id(requestId != null ? requestId : UUID.randomUUID().toString())
                .object("chat.completion.chunk")
                .created(System.currentTimeMillis() / 1000)
                .model(model != null ? model : "gpt-3.5-turbo")
                .choices(java.util.List.of(ShortLinkChatStreamRespDTO.Choice.builder()
                        .index(0)
                        .delta(ShortLinkChatStreamRespDTO.Delta.builder()
                                .content("抱歉，处理您的请求时出现错误: " + (errorMessage != null ? errorMessage : "未知错误"))
                                .build())
                        .finishReason("stop")
                        .build()))
                .build();
    }

    /**
     * 检查响应是否包含内容
     * @param response ChatResponse对象
     * @return 是否包含内容
     */
    public static boolean hasContent(ChatResponse response) {
        String content = response.getResult().getOutput().getText();
        return content != null && !content.trim().isEmpty();
    }

    /**
     * 记录响应统计信息
     * @param response ChatResponse对象
     * @param requestId 请求ID
     */
    public static void logResponseStats(ChatResponse response, String requestId) {
        if (response != null) {
            String content =response.getResult().getOutput().getText();
            log.debug("请求 {} 收到响应: 有效={}, 有内容={}, 内容长度={}", 
                    requestId, 
                    isValidResponse(response), 
                    hasContent(response),
                    content != null ? content.length() : 0);
        }
    }
} 
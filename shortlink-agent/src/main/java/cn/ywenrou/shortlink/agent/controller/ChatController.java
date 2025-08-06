package cn.ywenrou.shortlink.agent.controller;

import cn.ywenrou.shortlink.agent.dto.req.ShortLinkChatReqDTO;
import cn.ywenrou.shortlink.agent.dto.resp.ShortLinkChatStreamRespDTO;
import cn.ywenrou.shortlink.agent.service.SpringAiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 聊天控制器
 * 支持流式响应，类似ChatGPT、Ollama、Claude的API
 */
@RestController
@RequestMapping("/agent/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SpringAiService springAiService;
    private final ObjectMapper objectMapper;

    /**
     * 流式聊天接口
     * 返回Server-Sent Events格式的流式响应
     */
    @PostMapping(value = "/completions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody ShortLinkChatReqDTO request) {
        log.info("收到流式聊天请求: {}", request.getMessage());
        
        return springAiService.generateShortLinkContext(request)
                .map(response -> {
                    try {
                        String json = objectMapper.writeValueAsString(response);
                        return "data: " + json + "\n\n";
                    } catch (JsonProcessingException e) {
                        log.error("序列化响应失败", e);
                        return "data: {\"error\": \"序列化失败\"}\n\n";
                    }
                })
                .onErrorResume(e -> {
                    log.error("流式聊天处理失败", e);
                    return Flux.just("data: {\"error\": \"" + e.getMessage() + "\"}\n\n");
                });
    }


    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chat service is running");
    }

    /**
     * 创建错误响应
     */
    private ShortLinkChatStreamRespDTO createErrorResponse(String model, String errorMessage) {
        return ShortLinkChatStreamRespDTO.builder()
                .id("error_" + System.currentTimeMillis())
                .object("chat.completion.chunk")
                .created(System.currentTimeMillis() / 1000)
                .model(model)
                .choices(List.of(ShortLinkChatStreamRespDTO.Choice.builder()
                        .index(0)
                        .delta(ShortLinkChatStreamRespDTO.Delta.builder()
                                .content("抱歉，处理您的请求时出现错误: " + errorMessage)
                                .build())
                        .finishReason("stop")
                        .build()))
                .build();
    }

} 
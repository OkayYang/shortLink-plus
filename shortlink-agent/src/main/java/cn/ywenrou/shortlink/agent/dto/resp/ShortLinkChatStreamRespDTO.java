package cn.ywenrou.shortlink.agent.dto.resp;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 聊天流式响应对象
 * 参考ChatGPT、Ollama、Claude的流式返回格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkChatStreamRespDTO {

    /**
     * 响应ID
     */
    @JsonProperty("id")
    private String id;

    /**
     * 对象类型
     */
    @JsonProperty("object")
    private String object;

    /**
     * 创建时间
     */
    @JsonProperty("created")
    private Long created;

    /**
     * 模型名称
     */
    @JsonProperty("model")
    private String model;

    /**
     * 选择列表
     */
    @JsonProperty("choices")
    private List<ShortLinkChatStreamRespDTO.Choice> choices;

    /**
     * 使用情况
     */
    @JsonProperty("usage")
    private ShortLinkChatStreamRespDTO.Usage usage;

    /**
     * 系统指纹
     */
    @JsonProperty("system_fingerprint")
    private String systemFingerprint;

    /**
     * 选择对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        /**
         * 索引
         */
        @JsonProperty("index")
        private Integer index;

        /**
         * Delta对象
         */
        @JsonProperty("delta")
        private ShortLinkChatStreamRespDTO.Delta delta;

        /**
         * 完成原因
         */
        @JsonProperty("finish_reason")
        private String finishReason;

        /**
         * 日志概率
         */
        @JsonProperty("logprobs")
        private Object logprobs;
    }

    /**
     * Delta对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Delta {
        /**
         * 角色
         */
        @JsonProperty("role")
        private String role;

        /**
         * 内容
         */
        @JsonProperty("content")
        private String content;

        /**
         * 工具调用
         */
        @JsonProperty("tool_calls")
        private List<ShortLinkChatStreamRespDTO.ToolCall> toolCalls;
    }

    /**
     * 工具调用对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolCall {
        /**
         * 索引
         */
        @JsonProperty("index")
        private Integer index;

        /**
         * ID
         */
        @JsonProperty("id")
        private String id;

        /**
         * 类型
         */
        @JsonProperty("type")
        private String type;

        /**
         * 函数
         */
        @JsonProperty("function")
        private ShortLinkChatStreamRespDTO.Function function;
    }

    /**
     * 函数对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Function {
        /**
         * 名称
         */
        @JsonProperty("name")
        private String name;

        /**
         * 参数
         */
        @JsonProperty("arguments")
        private String arguments;
    }

    /**
     * 使用情况对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        /**
         * 提示令牌数
         */
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        /**
         * 完成令牌数
         */
        @JsonProperty("completion_tokens")
        private Integer completionTokens;

        /**
         * 总令牌数
         */
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }

    /**
     * 创建流式响应
     */
    public static ShortLinkChatStreamRespDTO createStreamResponse(String content, String model, String id) {
        return ShortLinkChatStreamRespDTO.builder()
                .id(id)
                .object("chat.completion.chunk")
                .created(System.currentTimeMillis() / 1000)
                .model(model)
                .choices(List.of(ShortLinkChatStreamRespDTO.Choice.builder()
                        .index(0)
                        .delta(ShortLinkChatStreamRespDTO.Delta.builder()
                                .content(content)
                                .build())
                        .finishReason(null)
                        .build()))
                .build();
    }

    /**
     * 创建完成响应
     */
    public static ShortLinkChatStreamRespDTO createFinishResponse(String model, String id) {
        return ShortLinkChatStreamRespDTO.builder()
                .id(id)
                .object("chat.completion.chunk")
                .created(System.currentTimeMillis() / 1000)
                .model(model)
                .choices(List.of(ShortLinkChatStreamRespDTO.Choice.builder()
                        .index(0)
                        .delta(ShortLinkChatStreamRespDTO.Delta.builder().build())
                        .finishReason("stop")
                        .build()))
                .build();
    }

    /**
     * 创建工具调用响应
     */
    public static ShortLinkChatStreamRespDTO createToolCallResponse(String model, String id, String toolName, String arguments) {
        return ShortLinkChatStreamRespDTO.builder()
                .id(id)
                .object("chat.completion.chunk")
                .created(System.currentTimeMillis() / 1000)
                .model(model)
                .choices(List.of(ShortLinkChatStreamRespDTO.Choice.builder()
                        .index(0)
                        .delta(ShortLinkChatStreamRespDTO.Delta.builder()
                                .toolCalls(List.of(ShortLinkChatStreamRespDTO.ToolCall.builder()
                                        .index(0)
                                        .id("call_" + System.currentTimeMillis())
                                        .type("function")
                                        .function(ShortLinkChatStreamRespDTO.Function.builder()
                                                .name(toolName)
                                                .arguments(arguments)
                                                .build())
                                        .build()))
                                .build())
                        .finishReason(null)
                        .build()))
                .build();
    }
}
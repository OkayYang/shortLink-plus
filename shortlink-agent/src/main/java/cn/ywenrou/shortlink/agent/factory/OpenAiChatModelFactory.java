package cn.ywenrou.shortlink.agent.factory;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

import static cn.ywenrou.shortlink.agent.Constants.ShortLinkAgentConstant.*;

@Component
public class OpenAiChatModelFactory {

    @Value("${spring.ai.openai.api-key}")
    private String openAiKey;

    @Value("${spring.ai.openai.base-url}")
    private String openAiBaseUrl;

    // 缓存不同模型的实例
    private final ConcurrentHashMap<String, OpenAiChatModel> modelCache = new ConcurrentHashMap<>();

    public OpenAiChatModel createModel(String modelName) {
        if (modelName == null) {
            modelName = DEFAULT_MODEL;
        }
        return modelCache.computeIfAbsent(modelName, name -> {
            OpenAiApi openAiApi = OpenAiApi.builder()
                    .apiKey(openAiKey)
                    .baseUrl(openAiBaseUrl)
                    .build();
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .model(name)
                    .maxTokens(MODEL_MAX_TOKEN)
                    .temperature(MODEL_TEMPERATURE)
                    .build();
            return OpenAiChatModel.builder()
                    .openAiApi(openAiApi)
                    .defaultOptions(options)
                    .build();
        });
    }
}
package cn.ywenrou.shortlink.agent.test;

import cn.ywenrou.shortlink.agent.ShortLinkAgentApplication;
import cn.ywenrou.shortlink.agent.service.SpringAiService;
import cn.ywenrou.shortlink.agent.tools.DateTimeTools;
import cn.ywenrou.shortlink.agent.tools.ShortLinkTools;
import cn.ywenrou.shortlink.agent.tools.WeatherTools;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * 短链接代理测试类
 * 测试AI对话和工具调用功能
 */
@SpringBootTest(classes = ShortLinkAgentApplication.class)
@TestPropertySource(properties = {
    "spring.ai.openai.api-key=sk-Vk97glBeYQjmlkb7dPhcYdNDdOZljSHYtEIytJqB536kb26u",
    "spring.ai.openai.base-url=https://chatapi.littlewheat.com"
})
public class ShortLinkAgentTest {

    @Autowired
    private OpenAiChatModel openAiChatModel;

    @Autowired
    private WeatherTools weatherTools;

    @Autowired
    private DateTimeTools dateTimeTools;

    @Autowired
    private ShortLinkTools shortLinkTools;

    @Autowired
    private SpringAiService springAiService;

//    @Test
//    public void testChat() {
//        try {
//            // 发送一个简单的消息
//            String message = "你好，请介绍一下你自己";
//            String response = openAiChatModel.call(message);
//
//            System.out.println("用户消息: " + message);
//            System.out.println("AI回复: " + response);
//        } catch (Exception e) {
//            System.err.println("测试失败: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testTools() {
//        try {
//            System.out.println("开始测试工具调用功能...");
//
//            // 使用ChatClient和工具调用
//            ChatClient chatClient = ChatClient.create(openAiChatModel);
//
//            // 测试天气查询
//            String weatherResponse = chatClient.prompt("北京今天天气怎么样？")
//                    .tools(weatherTools)
//                    .call()
//                    .content();
//
//            System.out.println("天气查询结果:");
//            System.out.println(weatherResponse);
//
//            // 测试天气预报
//            String forecastResponse = chatClient.prompt("请告诉我上海未来3天的天气预报")
//                    .tools(weatherTools)
//                    .call()
//                    .content();
//
//            System.out.println("天气预报结果:");
//            System.out.println(forecastResponse);
//
//            // 测试复杂查询
//            String complexResponse = chatClient.prompt("我想知道广州和深圳的天气对比，哪个城市更适合出行？")
//                    .tools(weatherTools)
//                    .call()
//                    .content();
//
//            System.out.println("复杂查询结果:");
//            System.out.println(complexResponse);
//
//        } catch (Exception e) {
//            System.err.println("工具调用测试失败: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testDateTimeTools() {
//        try {
//            System.out.println("开始测试日期时间工具...");
//
//            ChatClient chatClient = ChatClient.create(openAiChatModel);
//
//            // 测试当前时间查询
//            String timeResponse = chatClient.prompt("现在几点了？")
//                    .tools(dateTimeTools)
//                    .call()
//                    .content();
//
//            System.out.println("时间查询结果:");
//            System.out.println(timeResponse);
//
//            // 测试日期查询
//            String dateResponse = chatClient.prompt("今天是几号？")
//                    .tools(dateTimeTools)
//                    .call()
//                    .content();
//
//            System.out.println("日期查询结果:");
//            System.out.println(dateResponse);
//
//        } catch (Exception e) {
//            System.err.println("日期时间工具测试失败: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testAllTools() {
//        try {
//            System.out.println("开始测试所有工具...");
//
//            ChatClient chatClient = ChatClient.create(openAiChatModel);
//
//            // 测试同时使用多个工具
//            String response = chatClient.prompt("现在几点了？北京天气怎么样？")
//                    .tools(weatherTools, dateTimeTools)
//                    .call()
//                    .content();
//
//            System.out.println("多工具调用结果:");
//            System.out.println(response);
//
//        } catch (Exception e) {
//            System.err.println("多工具测试失败: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testToolWithoutTools() {
//        try {
//            System.out.println("开始测试不使用工具的情况...");
//
//            ChatClient chatClient = ChatClient.create(openAiChatModel);
//
//            // 不使用工具，直接询问天气
//            String response = chatClient.prompt("北京今天天气怎么样？")
//                    .call()
//                    .content();
//
//            System.out.println("不使用工具的回答:");
//            System.out.println(response);
//
//        } catch (Exception e) {
//            System.err.println("测试失败: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testIntelligentContentGeneration() {
//        try {
//            System.out.println("开始测试智能内容生成功能...");
//
//            ChatClient chatClient = ChatClient.create(openAiChatModel);
//
//            // 测试场景2：短链接处理https://blog.ywenrou.cn/
//            String userInput2 = "这个短链接内容不错，https://blog.ywenrou.cn/请帮我写一个技术博客的推广文案";
//
//            ChatResponse response2 = chatClient.prompt(userInput2)
//                    .tools(dateTimeTools, weatherTools, shortLinkTools)
//                    .call().chatResponse();
//
//            System.out.println("测试场景2 - 用户输入: " + userInput2);
//            System.out.println("AI回复:" + response2.getResult().getOutput().getText());
//            System.out.println("是否工具调用：" + response2.hasToolCalls());
//
//
//        } catch (Exception e) {
//            System.err.println("智能内容生成测试失败: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//    @Test
//    public void testStreamChat() {
//        System.out.println("🚀 开始测试流式聊天功能");
//        System.out.println("=".repeat(50));
//
//        String message = "请帮我为这个链接 https://www.baidu.com 写一个吸引人的推广文案";
//
//        System.out.println("📝 用户输入: " + message);
//        System.out.println();
//
//        System.out.println("🔄 开始流式响应:");
//        System.out.println("-".repeat(30));
//        ShortLinkChatReqDTO shortLinkChatReqDTO = new ShortLinkChatReqDTO();
//        shortLinkChatReqDTO.setMessage(message);
//        shortLinkChatReqDTO.setModel("gpt-3.5-turbo");
//
//
//
//        springAiService.generateShortLinkContext(shortLinkChatReqDTO)
//                .doOnNext(response -> {
//                    System.out.println("📦 响应块:");
//                    System.out.println("  ID: " + response.getId());
//                    System.out.println("  模型: " + response.getModel());
//                    System.out.println("  内容: " + (response.getChoices() != null && !response.getChoices().isEmpty()
//                            ? response.getChoices().get(0).getDelta().getContent() : ""));
//                    System.out.println("  完成原因: " + (response.getChoices() != null && !response.getChoices().isEmpty()
//                            ? response.getChoices().get(0).getFinishReason() : ""));
//                    System.out.println();
//                })
//                .doOnComplete(() -> {
//                    System.out.println("✅ 流式响应完成");
//                    System.out.println("=".repeat(50));
//                })
//                .doOnError(error -> {
//                    System.err.println("❌ 流式响应错误: " + error.getMessage());
//                })
//                .blockLast();
//    }
}

package cn.ywenrou.shortlink.agent.tools;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 内容生成工具类
 * 用于提取URL、抓取网页内容并生成文案
 */
@Slf4j
@Component
public class ShortLinkTools {

    /**
     * 从用户输入中提取URL
     * @param userInput 用户输入的内容
     * @return 提取到的URL，如果没有找到返回null
     */
    @Tool(description = "Extract URL from user input text")
    public String extractUrl(String userInput) {
        try {
            // URL正则表达式，匹配http和https链接
            String urlPattern = "(https?://[\\w\\-./?=&%#]+)";
            Pattern pattern = Pattern.compile(urlPattern);
            Matcher matcher = pattern.matcher(userInput);
            
            if (matcher.find()) {
                String url = matcher.group(1);
                log.info("从用户输入中提取到URL: {}", url);
                return url;
            }
            
            log.warn("未从用户输入中找到URL: {}", userInput);
            return null;
        } catch (Exception e) {
            log.error("提取URL失败", e);
            return null;
        }
    }

    /**
     * 抓取网页内容
     * @param url 网页URL
     * @return 网页的主要内容
     */
    @Tool(description = "Fetch and extract main content from a web page")
    public String fetchWebContent(String url) {
        try {
            log.info("开始抓取网页内容: {}", url);
            
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();

            // 移除脚本和样式标签
            doc.select("script, style, nav, footer, header, .ad, .advertisement").remove();
            
            // 提取主要内容
            String title = doc.title();
            String content = extractMainContent(doc);
            
            String result = String.format("网页标题: %s\n\n主要内容: %s", title, content);
            log.info("网页内容抓取成功，标题: {}", title);
            
            return result;
            
        } catch (IOException e) {
            log.error("抓取网页内容失败: {}", url, e);
            return "无法抓取网页内容，请检查URL是否正确或网络连接是否正常";
        } catch (Exception e) {
            log.error("抓取网页内容时发生未知错误: {}", url, e);
            return "抓取网页内容时发生错误: " + e.getMessage();
        }
    }

    /**
     * 解析短链接获取原始URL
     * @param shortUrl 短链接
     * @return 原始URL
     */
    @Tool(description = "Resolve short URL to get the original URL")
    public String resolveShortUrl(String shortUrl) {
        try {
            log.info("开始解析短链接: {}", shortUrl);
            
            // 跟踪HTTP重定向获取最终URL
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new URL(shortUrl).openConnection();
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 400) {
                String originalUrl = connection.getURL().toString();
                log.info("短链接解析成功: {} -> {}", shortUrl, originalUrl);
                return originalUrl;
            }
            
            log.warn("短链接解析失败，响应码: {}", responseCode);
            return shortUrl;
            
        } catch (Exception e) {
            log.error("解析短链接失败: {}", shortUrl, e);
            return shortUrl;
        }
    }

    /**
     * 根据网页内容和用户需求生成文案
     * @param webContent 网页内容
     * @param userRequirement 用户需求
     * @return 生成的文案
     */
    @Tool(description = "Generate content based on web page content and user requirements")
    public String generateContent(String webContent, String userRequirement) {
        try {
            log.info("开始生成文案，用户需求: {}", userRequirement);
            
            // 这里可以调用AI服务来生成文案
            // 目前返回一个基于内容的简单文案
            String generatedContent = String.format("""
                基于网页内容为您生成的文案：
                
                网页内容摘要：%s
                
                根据您的需求"%s"，我为您生成了以下文案：
                
                【推荐文案】
                %s
                
                【文案特点】
                - 基于网页真实内容
                - 符合您的需求描述
                - 语言自然流畅
                - 适合传播分享
                """, 
                webContent.length() > 200 ? webContent.substring(0, 200) + "..." : webContent,
                userRequirement,
                generateSimpleContent(webContent, userRequirement)
            );
            
            log.info("文案生成成功");
            return generatedContent;
            
        } catch (Exception e) {
            log.error("生成文案失败", e);
            return "生成文案时发生错误: " + e.getMessage();
        }
    }

    /**
     * 提取网页主要内容
     */
    private String extractMainContent(Document doc) {
        // 尝试多种选择器来获取主要内容
        String[] selectors = {
            "article",
            ".content",
            ".main-content",
            ".post-content",
            ".entry-content",
            "main",
            ".article-content",
            "#content",
            ".text-content",
            ".post-body",
            ".article-body"
        };

        for (String selector : selectors) {
            Elements elements = doc.select(selector);
            if (!elements.isEmpty()) {
                String content = elements.first().text().trim();
                if (content.length() > 50) { // 确保内容足够长
                    return content;
                }
            }
        }

        // 如果没有找到特定的内容区域，提取body文本
        Element body = doc.body();
        if (body != null) {
            return body.text().trim();
        }

        return doc.text().trim();
    }

    /**
     * 生成简单的文案内容
     */
    private String generateSimpleContent(String webContent, String userRequirement) {
        // 简单的文案生成逻辑
        String[] templates = {
            "🔥 发现了一个超棒的内容！%s 这个网站真的很不错，%s",
            "📱 分享一个好东西：%s 根据%s的需求，这个内容绝对值得一看！",
            "💡 推荐：%s 这个网站内容很丰富，%s",
            "🌟 好东西要分享：%s 特别适合%s的朋友们！"
        };
        
        int templateIndex = (int) (Math.random() * templates.length);
        String template = templates[templateIndex];
        
        // 提取网页标题（如果有的话）
        String title = "这个网站";
        if (webContent.contains("网页标题:")) {
            String[] parts = webContent.split("网页标题:");
            if (parts.length > 1) {
                String titlePart = parts[1].split("\n")[0].trim();
                if (!titlePart.isEmpty()) {
                    title = titlePart;
                }
            }
        }
        
        return String.format(template, title, userRequirement);
    }
} 
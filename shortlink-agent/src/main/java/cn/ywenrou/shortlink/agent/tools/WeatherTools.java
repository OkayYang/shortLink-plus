package cn.ywenrou.shortlink.agent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * 天气工具类
 * 提供天气查询功能
 */
@Component
public class WeatherTools {
    
    @Tool(description = "Get the current weather information for a specific location")
    public String getWeatherInLocation(String location) {
        // 这里可以集成真实的天气API，比如和风天气、OpenWeatherMap等
        // 目前返回模拟数据
        return String.format("当前%s的天气情况：多云，温度25°C，湿度60%%，风速3级", location);
    }
    
    @Tool(description = "Get the weather forecast for the next few days in a location")
    public String getWeatherForecast(String location, int days) {
        // 返回天气预报
        return String.format("%s未来%d天的天气预报：明天晴天，后天多云，大后天小雨", location, days);
    }
}

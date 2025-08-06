package cn.ywenrou.shortlink.console.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class FileResourceConfig implements WebMvcConfigurer {
    
    private final FileUploadConfig fileUploadConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件访问路径映射
        registry.addResourceHandler(fileUploadConfig.getUrlPrefix() + "**")
                .addResourceLocations("file:" + fileUploadConfig.getPath());
    }
} 
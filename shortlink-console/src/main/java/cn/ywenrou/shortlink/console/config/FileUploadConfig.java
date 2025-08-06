package cn.ywenrou.shortlink.console.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {
    
    /**
     * 文件上传路径
     */
    private String path = "uploads/";
    
    /**
     * 头像上传路径
     */
    private String avatarPath = "uploads/avatar/";
    
    /**
     * 文件访问前缀
     */
    private String urlPrefix = "/files/";
    
    /**
     * 最大文件大小（字节）
     */
    private long maxSize = 5 * 1024 * 1024; // 5MB
    
    /**
     * 允许的图片文件类型
     */
    private String[] allowedImageTypes = {"jpg", "jpeg", "png", "gif", "bmp"};
} 
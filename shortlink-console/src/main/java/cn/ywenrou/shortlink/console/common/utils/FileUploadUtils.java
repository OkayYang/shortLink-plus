package cn.ywenrou.shortlink.console.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.ywenrou.shortlink.common.core.exception.ClientException;
import cn.ywenrou.shortlink.console.config.FileUploadConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileUploadUtils {
    
    private final FileUploadConfig fileUploadConfig;
    
    /**
     * 上传头像文件
     * @param file 上传的文件
     * @return 文件访问URL
     */
    public String uploadAvatar(MultipartFile file) {
        return uploadFile(file, fileUploadConfig.getAvatarPath());
    }
    
    /**
     * 上传文件
     * @param file 上传的文件
     * @param subPath 子路径
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String subPath) {
        // 验证文件
        validateFile(file);
        
        // 生成文件名
        String fileName = generateFileName(file.getOriginalFilename());
        
        // 构建完整路径
        String fullPath = buildFilePath(subPath, fileName);
        
        try {
            // 确保目录存在
            File targetFile = new File(fullPath);
            FileUtil.mkParentDirs(targetFile);
            
            // 保存文件
            file.transferTo(targetFile);
            
            log.info("文件上传成功: {}", fullPath);
            
            // 返回访问URL
            return buildFileUrl(subPath, fileName);
            
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new ClientException("文件上传失败");
        }
    }
    
    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ClientException("文件不能为空");
        }
        
        if (file.getSize() > fileUploadConfig.getMaxSize()) {
            throw new ClientException("文件大小不能超过" + (fileUploadConfig.getMaxSize() / 1024 / 1024) + "MB");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new ClientException("文件名不能为空");
        }
        
        String extension = FileUtil.extName(originalFilename).toLowerCase();
        if (!Arrays.asList(fileUploadConfig.getAllowedImageTypes()).contains(extension)) {
            throw new ClientException("不支持的文件类型，只支持: " + String.join(", ", fileUploadConfig.getAllowedImageTypes()));
        }
    }
    
    /**
     * 生成文件名
     */
    private String generateFileName(String originalFilename) {
        String extension = FileUtil.extName(originalFilename);
        String uuid = IdUtil.fastSimpleUUID();
        return uuid + "." + extension;
    }
    
    /**
     * 构建文件路径
     */
    private String buildFilePath(String subPath, String fileName) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return fileUploadConfig.getPath() + subPath + datePath + "/" + fileName;
    }
    
    /**
     * 构建文件访问URL
     */
    private String buildFileUrl(String subPath, String fileName) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return fileUploadConfig.getUrlPrefix() + subPath + datePath + "/" + fileName;
    }
    
    /**
     * 删除文件
     */
    public boolean deleteFile(String fileUrl) {
        try {
            if (StrUtil.isBlank(fileUrl)) {
                return true;
            }
            
            // 从URL转换为文件路径
            String filePath = fileUrl.replace(fileUploadConfig.getUrlPrefix(), fileUploadConfig.getPath());
            File file = new File(filePath);
            
            if (file.exists()) {
                boolean deleted = file.delete();
                log.info("删除文件: {} {}", filePath, deleted ? "成功" : "失败");
                return deleted;
            }
            return true;
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage(), e);
            return false;
        }
    }
} 
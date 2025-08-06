package cn.ywenrou.shortlink.console.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortLinkUpdateReqDTO {

    /**
     * 域名
     */
    private String domain;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 短链接
     */
    private String shortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    /**
     * 描述
     */
    private String describe;

    /**
     * 网站图标
     */
    private String favicon;

    /**
     * 启用状态 0：启用 1：未启用
     */
    private Integer enableStatus;
    
    /**
     * 用户名
     */
    private String username;
}

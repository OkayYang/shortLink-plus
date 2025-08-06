package cn.ywenrou.shortlink.system.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortLinkDeleteReqDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 短链接
     */
    private String shortUrl;
    
    /**
     * 用户名
     */
    private String username;
}

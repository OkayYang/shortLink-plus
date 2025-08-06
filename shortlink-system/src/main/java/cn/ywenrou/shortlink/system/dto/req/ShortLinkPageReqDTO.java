package cn.ywenrou.shortlink.system.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortLinkPageReqDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 当前页
     */
    private Long current;

    /**
     * 每页数量
     */
    private Long size;
    
    /**
     * 用户名
     */
    private String username;
}

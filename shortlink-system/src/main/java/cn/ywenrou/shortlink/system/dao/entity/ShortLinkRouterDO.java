package cn.ywenrou.shortlink.system.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@TableName("sl_link_router")
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkRouterDO {
    private Long id;

    private String fullShortUrl;

    private String gid;
}

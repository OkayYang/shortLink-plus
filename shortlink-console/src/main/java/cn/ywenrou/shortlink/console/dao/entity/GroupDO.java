package cn.ywenrou.shortlink.console.dao.entity;

import cn.ywenrou.shortlink.common.core.web.dao.BaseDO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 短链接分组实体
 */
@Data
@TableName("sl_group")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;
    /**
     * 分组描述
     */
    private String description;

    /**
     * 分组颜色/icon
     */
    private String tag;

    /**
     * 创建分组用户名
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;
}

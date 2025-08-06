package cn.ywenrou.shortlink.common.core.web.dao;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 数据库持久层对象基础属性，包含创建时间、更新时间和删除标识
 *
 * @author xuxiaoyang
 */
@Data
@SuperBuilder // 添加此注解
@NoArgsConstructor // 需要添加无参构造函数
@AllArgsConstructor // 需要添加全参构造函数
public class BaseDO {

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标识 0：未删除 1：已删除
     */
    @TableField(fill = FieldFill.INSERT)
    @TableLogic
    private Integer delFlag;

    /**
     * 删除时间
     */
    private Date delTime;
}

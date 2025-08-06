package cn.ywenrou.shortlink.console.dao.entity;

import cn.ywenrou.shortlink.common.core.web.dao.BaseDO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户持久层实体
 */
@Data
@TableName("sl_user")
public class UserDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 头像
     */
    private String avatar;

}

package cn.ywenrou.shortlink.console.dto.req;

import lombok.Data;

@Data
public class UserUpdateReqDTO {
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
}

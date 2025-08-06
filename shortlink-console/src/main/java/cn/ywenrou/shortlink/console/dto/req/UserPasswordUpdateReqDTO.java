package cn.ywenrou.shortlink.console.dto.req;

import lombok.Data;

@Data
public class UserPasswordUpdateReqDTO {
    /**
     * 原密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;
} 
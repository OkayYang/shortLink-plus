package cn.ywenrou.shortlink.console.dto.req;

import lombok.Data;

@Data
public class UserRegisterReqDTO {
    private String username;
    private String password;
    private String mail;
    private String code;
}

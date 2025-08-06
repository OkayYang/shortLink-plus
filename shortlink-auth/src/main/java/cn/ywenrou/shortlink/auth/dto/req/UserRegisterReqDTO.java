package cn.ywenrou.shortlink.auth.dto.req;

import lombok.Data;

@Data
public class UserRegisterReqDTO {
    private String username;
    private String password;
    private String mail;
    private String code;
}

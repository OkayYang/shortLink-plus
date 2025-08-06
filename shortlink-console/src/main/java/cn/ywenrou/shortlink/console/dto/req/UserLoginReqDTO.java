package cn.ywenrou.shortlink.console.dto.req;

import lombok.Data;

@Data
public class UserLoginReqDTO {
    private String username;
    private String password;
}

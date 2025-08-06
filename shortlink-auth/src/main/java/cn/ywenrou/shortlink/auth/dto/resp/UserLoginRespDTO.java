package cn.ywenrou.shortlink.auth.dto.resp;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserLoginRespDTO {
    private String token;
    private String loginIp;
    private Long loginTime;
    private Long expireTime;
}

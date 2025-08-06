package cn.ywenrou.shortlink.console.service;

import cn.ywenrou.shortlink.console.dao.entity.UserDO;
import cn.ywenrou.shortlink.console.dto.req.UserLoginReqDTO;
import cn.ywenrou.shortlink.console.dto.req.UserPasswordUpdateReqDTO;
import cn.ywenrou.shortlink.console.dto.req.UserRegisterReqDTO;
import cn.ywenrou.shortlink.console.dto.req.UserUpdateReqDTO;
import cn.ywenrou.shortlink.console.dto.resp.UserInfoRespDTO;
import cn.ywenrou.shortlink.console.dto.resp.UserLoginRespDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends IService<UserDO> {

    boolean hasUsername(String username);
    void register(UserRegisterReqDTO requestParam);
    void update(UserUpdateReqDTO requestParam);
    void updatePassword(UserPasswordUpdateReqDTO requestParam);
    String updateAvatar(MultipartFile file);
    UserLoginRespDTO login(UserLoginReqDTO requestParam);
    void logout();
    void refreshToken();
    UserInfoRespDTO getUserInfo();





}

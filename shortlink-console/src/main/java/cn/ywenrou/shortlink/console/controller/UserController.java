package cn.ywenrou.shortlink.console.controller;

import cn.ywenrou.shortlink.common.core.domain.AjaxResult;
import cn.ywenrou.shortlink.common.core.web.controller.BaseController;
import cn.ywenrou.shortlink.common.security.utils.SecurityUtils;
import cn.ywenrou.shortlink.console.dto.req.UserLoginReqDTO;
import cn.ywenrou.shortlink.console.dto.req.UserPasswordUpdateReqDTO;
import cn.ywenrou.shortlink.console.dto.req.UserRegisterReqDTO;
import cn.ywenrou.shortlink.console.dto.req.UserUpdateReqDTO;
import cn.ywenrou.shortlink.console.remote.RemoteSystemService;
import cn.ywenrou.shortlink.console.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController extends BaseController {
    private final UserService userService;
    private final RemoteSystemService remoteSystemService;

    @GetMapping("/has-username")
    public AjaxResult hasUsername(@RequestParam("username") String username) {
        return success(userService.hasUsername(username));
    }

    @PostMapping("/register")
    public AjaxResult register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return success("注册成功");
    }

    @PutMapping("/update")
    public AjaxResult update(@RequestBody UserUpdateReqDTO requestParam) {
        userService.update(requestParam);
        return success("用户信息更新成功");
    }

    @PutMapping("/update-password")
    public AjaxResult updatePassword(@RequestBody UserPasswordUpdateReqDTO requestParam) {
        userService.updatePassword(requestParam);
        return success("密码修改成功");
    }

    @PostMapping("/upload-avatar")
    public AjaxResult uploadAvatar(@RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.updateAvatar(file);
        return success(avatarUrl);
    }

    @DeleteMapping("/logout")
    public AjaxResult logout() {
        userService.logout();
        return success("退出成功");
    }

    @PostMapping("/refresh")
    public AjaxResult refreshToken() {
        userService.refreshToken();
        return success("刷新成功");
    }

    @GetMapping("/getInfo")
    public AjaxResult getUserInfo() {
        return success(userService.getUserInfo());
    }

    @PostMapping("/login")
    public AjaxResult login(@RequestBody UserLoginReqDTO requestParam) {
        return success(userService.login(requestParam));

    }
    
    /**
     * 获取用户短链接统计信息
     */
    @GetMapping("/stats")
    public AjaxResult getUserStats() {
        String username = SecurityUtils.getUsername();
        return remoteSystemService.getUserStats(username);
    }
}

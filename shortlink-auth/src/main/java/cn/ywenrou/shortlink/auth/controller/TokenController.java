package cn.ywenrou.shortlink.auth.controller;

import cn.ywenrou.shortlink.auth.dto.req.UserLoginReqDTO;
import cn.ywenrou.shortlink.auth.dto.req.UserRegisterReqDTO;
import cn.ywenrou.shortlink.auth.remote.RemoteUserService;
import cn.ywenrou.shortlink.common.core.domain.AjaxResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TokenController
{
    private final RemoteUserService remoteUserService;

    @PostMapping("/login")
    public AjaxResult login(@RequestBody UserLoginReqDTO requestParam)
    {
        return remoteUserService.login(requestParam);
    }

    @DeleteMapping("/logout")
    public AjaxResult logout()
    {
        return remoteUserService.logout();
    }

    @PostMapping("/refresh")
    public AjaxResult refresh()
    {
        return remoteUserService.refreshToken();
    }
    @GetMapping("/getInfo")
    public AjaxResult getUserInfo(){
        return remoteUserService.getUserInfo();
    }
    @PostMapping("/register")
    public AjaxResult register(@RequestBody UserRegisterReqDTO registerBody)
    {
        return remoteUserService.registerUser(registerBody);
    }
    @GetMapping("/has-username")
    public AjaxResult hasUsername(@RequestParam("username") String username){
        return remoteUserService.hasUsername(username);
    }
}

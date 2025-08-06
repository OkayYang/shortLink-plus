package cn.ywenrou.shortlink.auth.remote;


import cn.ywenrou.shortlink.auth.dto.req.UserLoginReqDTO;
import cn.ywenrou.shortlink.auth.dto.req.UserRegisterReqDTO;
import cn.ywenrou.shortlink.auth.remote.factory.RemoteUserFallbackFactory;
import cn.ywenrou.shortlink.common.core.domain.AjaxResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务
 * 
 * @author xiaoyang
 */
@FeignClient(contextId = "remoteUserService", value = "shortlink-console", fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService
{

    /**
     * 注册用户信息
     *
     * @param userRegisterReqDTO 用户信息
     * @return 结果
     */
    @PostMapping("/user/register")
    public AjaxResult registerUser(@RequestBody UserRegisterReqDTO userRegisterReqDTO);

    /**
     * 登录
     *
     * @param userLoginReqDTO 用户信息
     * @return 结果
     */
    @PostMapping("/user/login")
    public AjaxResult login(@RequestBody UserLoginReqDTO userLoginReqDTO);

    /**
     * 刷新令牌
     *
     * @return 结果
     */
    @PostMapping("/user/refresh")
    public AjaxResult refreshToken();

    /**
     * 登出
     *
     * @return 结果
     */
    @PostMapping("/user/logout")
    public AjaxResult logout();

    /**
     * 判断用户名是否注册过
     */
    @GetMapping("/user/has-username")
    public AjaxResult hasUsername(@RequestParam("username") String username);

    @GetMapping("/user/getInfo")
    public AjaxResult getUserInfo();


}

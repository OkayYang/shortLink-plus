package cn.ywenrou.shortlink.auth.remote.factory;


import cn.ywenrou.shortlink.auth.dto.req.UserLoginReqDTO;
import cn.ywenrou.shortlink.auth.dto.req.UserRegisterReqDTO;
import cn.ywenrou.shortlink.auth.remote.RemoteUserService;
import cn.ywenrou.shortlink.common.core.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 用户服务降级处理
 *
 * @author xuxiaoyang
 */
@Component
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteUserFallbackFactory.class);

    @Override
    public RemoteUserService create(Throwable throwable)
    {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteUserService()
        {
            @Override
            public AjaxResult registerUser(UserRegisterReqDTO userRegisterReqDTO) {
                return AjaxResult.error("注册用户失败:" + throwable.getMessage());
            }

            @Override
            public AjaxResult login(UserLoginReqDTO userLoginReqDTO) {
                return AjaxResult.error("用户登录失败:" + throwable.getMessage());
            }

            @Override
            public AjaxResult refreshToken() {
                return AjaxResult.error("刷新token失败:" + throwable.getMessage());
            }

            @Override
            public AjaxResult logout() {
                return AjaxResult.error("退出登录失败:" + throwable.getMessage());
            }

            @Override
            public AjaxResult hasUsername(String username) {
                return  AjaxResult.error("查询失败:" + throwable.getMessage());
            }

            @Override
            public AjaxResult getUserInfo() {
                return  AjaxResult.error("获取用户信息失败:" + throwable.getMessage());
            }
        };
    }
}

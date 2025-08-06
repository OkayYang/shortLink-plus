package cn.ywenrou.shortlink.console.remote.factory;

import cn.ywenrou.shortlink.common.core.domain.AjaxResult;
import cn.ywenrou.shortlink.common.core.exception.RemoteException;
import cn.ywenrou.shortlink.console.dto.req.ShortLinkCreateReqDTO;
import cn.ywenrou.shortlink.console.dto.req.ShortLinkDeleteReqDTO;
import cn.ywenrou.shortlink.console.dto.req.ShortLinkUpdateReqDTO;
import cn.ywenrou.shortlink.console.remote.RemoteSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 短链接服务降级
 * @author xuxiaoyang
 */
@Slf4j
@Component
public class RemoteSystemFallbackFactory implements FallbackFactory<RemoteSystemService> {
    @Override
    public RemoteSystemService create(Throwable throwable) {
        log.error("System服务调用失败:{}", throwable.getMessage());
        return new RemoteSystemService() {
            @Override
            public AjaxResult createShortLink(ShortLinkCreateReqDTO requestParam) {
                return AjaxResult.error(new RemoteException("创建短链接失败").getMessage());
            }

            @Override
            public AjaxResult deleteShortLink(ShortLinkDeleteReqDTO requestParam) {
                return AjaxResult.error(new RemoteException("删除短链接失败").getMessage());
            }

            @Override
            public AjaxResult updateShortLink(ShortLinkUpdateReqDTO requestParam) {
                return AjaxResult.error(new RemoteException("更新短链接失败").getMessage());
            }

            @Override
            public AjaxResult listShortLink(String gid, Long current, Long size, String username) {
                return AjaxResult.error(new RemoteException("查询短链接列表失败").getMessage());
            }
            
            @Override
            public AjaxResult getUserStats(String username) {
                return AjaxResult.error(new RemoteException("获取用户统计信息失败").getMessage());
            }
        };
    }
}

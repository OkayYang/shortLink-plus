package cn.ywenrou.shortlink.system.service;

import cn.ywenrou.shortlink.system.dao.entity.ShortLinkDO;
import cn.ywenrou.shortlink.system.dto.req.ShortLinkCreateReqDTO;
import cn.ywenrou.shortlink.system.dto.req.ShortLinkDeleteReqDTO;
import cn.ywenrou.shortlink.system.dto.req.ShortLinkPageReqDTO;
import cn.ywenrou.shortlink.system.dto.req.ShortLinkUpdateReqDTO;
import cn.ywenrou.shortlink.system.dto.resp.ShortLinkCreateRespDTO;
import cn.ywenrou.shortlink.system.dto.resp.ShortLinkPageRespDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface SystemShortLinkService extends IService<ShortLinkDO> {

    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
    public void deleteShortLink(ShortLinkDeleteReqDTO requestParam);
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam);
    public IPage<ShortLinkPageRespDTO> listShortLink(ShortLinkPageReqDTO requestParam);
    public void redirectShortLink(String shortUri, HttpServletRequest request, HttpServletResponse response);
}

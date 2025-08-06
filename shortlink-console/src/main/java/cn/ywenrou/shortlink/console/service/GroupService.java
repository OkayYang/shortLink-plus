package cn.ywenrou.shortlink.console.service;

import cn.ywenrou.shortlink.console.dao.entity.GroupDO;
import cn.ywenrou.shortlink.console.dto.req.GroupCreateReqDTO;
import cn.ywenrou.shortlink.console.dto.req.GroupUpdateReqDTO;
import cn.ywenrou.shortlink.console.dto.resp.GroupInfoRespDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface GroupService extends IService<GroupDO> {
    void createGroup(GroupCreateReqDTO requestParam);
    void updateGroup(GroupUpdateReqDTO requestParam);
    void deleteGroup(String gid);
    List<GroupInfoRespDTO> listGroups();
    
    /**
     * 验证分组是否存在且属于当前用户
     * @param gid 分组ID
     * @return 是否有效
     */
    boolean validateGroup(String gid);
    
    /**
     * 获取用户默认分组ID
     * @return 默认分组ID
     */
    String getDefaultGroup();
}

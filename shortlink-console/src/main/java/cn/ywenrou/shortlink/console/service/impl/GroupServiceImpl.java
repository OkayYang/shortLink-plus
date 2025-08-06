package cn.ywenrou.shortlink.console.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.ywenrou.shortlink.common.core.exception.ClientException;
import cn.ywenrou.shortlink.console.dao.entity.GroupDO;
import cn.ywenrou.shortlink.console.dao.mapper.GroupMapper;
import cn.ywenrou.shortlink.console.dto.req.GroupCreateReqDTO;
import cn.ywenrou.shortlink.console.dto.req.GroupUpdateReqDTO;
import cn.ywenrou.shortlink.console.dto.resp.GroupInfoRespDTO;
import cn.ywenrou.shortlink.console.service.GroupService;
import cn.ywenrou.shortlink.common.security.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static cn.ywenrou.shortlink.common.core.enums.ErrorCodes.GROUP_NAME_EXIST_ERROR;
import static cn.ywenrou.shortlink.console.common.constant.RedisCacheConstants.LOCK_GROUP_CREATE_KEY;
import static cn.ywenrou.shortlink.console.common.constant.ShortLinkConstants.DEFAULT_GROUP_NAME;
import static cn.ywenrou.shortlink.console.common.constant.ShortLinkConstants.MAX_GROUP_NUM;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    private final RedissonClient redissonClient;
    @Override
    public void createGroup(GroupCreateReqDTO requestParam) {
        if (DEFAULT_GROUP_NAME.equals(requestParam.getName())) {
            throw new ClientException(GROUP_NAME_EXIST_ERROR);
        }
        String username = SecurityUtils.getUsername();
        /***
         * 这里采用分布式锁的目的防止超出分组数量限制：
         * 代码中限制每个用户最多只能创建10个分组
         * 如果没有锁，多个并发请求可能同时通过数量检查，导致实际创建的分组超过限制
         */
        RLock lock = redissonClient.getLock(LOCK_GROUP_CREATE_KEY+username);
        lock.lock();
        try {
            LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getUsername, username);
            List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
            if (CollUtil.isNotEmpty(groupDOList) && groupDOList.size() == MAX_GROUP_NUM) {
                throw new ClientException(String.format("已超出最大分组数：%d", MAX_GROUP_NUM));
            }
            String uuid = IdUtil.simpleUUID();
            GroupDO groupDO = BeanUtil.toBean(requestParam, GroupDO.class);
            groupDO.setGid(uuid);
            groupDO.setSortOrder(groupDOList.size() + 1);
            groupDO.setDescription(requestParam.getDescription());
            groupDO.setTag(requestParam.getTag());
            groupDO.setUsername(username);
            baseMapper.insert(groupDO);
        }catch (DuplicateKeyException ex) {
            throw new ClientException(GROUP_NAME_EXIST_ERROR);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void updateGroup(GroupUpdateReqDTO requestParam) {
        if (DEFAULT_GROUP_NAME.equals(requestParam.getName())) {
            throw new ClientException(GROUP_NAME_EXIST_ERROR);
        }
        String username = SecurityUtils.getUsername();
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername,username)
                .eq(GroupDO::getGid, requestParam.getGid());
        GroupDO groupDO = new GroupDO();
        groupDO.setName(requestParam.getName());
        groupDO.setSortOrder(requestParam.getSortOrder());
        groupDO.setDescription(requestParam.getDescription());
        groupDO.setTag(requestParam.getTag());
        try {
            if (baseMapper.update(groupDO, updateWrapper)==0) {
                throw new ClientException("更新失败");
            }
        }catch (DuplicateKeyException ex) {
            throw new ClientException(GROUP_NAME_EXIST_ERROR);
        }
    }

    @Override
    public void deleteGroup(String gid) {
        String username = SecurityUtils.getUsername();
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .set(GroupDO::getDelFlag, 1)
                .eq(GroupDO::getUsername,username)
                .eq(GroupDO::getGid, gid);
        GroupDO groupDO = GroupDO.builder().delTime(DateUtil.date()).build();
        if (baseMapper.update(groupDO, updateWrapper)==0) {
            throw new ClientException("删除失败");
        }
    }

    @Override
    public List<GroupInfoRespDTO> listGroups() {
        String username = SecurityUtils.getUsername();
        // 按照sortOrder字段升序排序
        return baseMapper.selectList(Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, username)
                .orderByAsc(GroupDO::getSortOrder))
                .stream()
                .map(groupDO -> BeanUtil.toBean(groupDO, GroupInfoRespDTO.class))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean validateGroup(String gid) {
        String username = SecurityUtils.getUsername();
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getGid, gid);
        
        return baseMapper.exists(queryWrapper);
    }
    
    @Override
    public String getDefaultGroup() {
        String username = SecurityUtils.getUsername();
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getName, DEFAULT_GROUP_NAME);
        
        GroupDO groupDO = baseMapper.selectOne(queryWrapper);
        return groupDO.getGid();
    }
}

package cn.ywenrou.shortlink.console.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.ywenrou.shortlink.common.core.constant.CacheConstants;
import cn.ywenrou.shortlink.common.core.constant.SecurityConstants;
import cn.ywenrou.shortlink.common.core.exception.ClientException;
import cn.ywenrou.shortlink.common.core.exception.ServiceException;
import cn.ywenrou.shortlink.common.core.utils.IpUtils;
import cn.ywenrou.shortlink.common.core.utils.JwtUtils;
import cn.ywenrou.shortlink.common.redis.service.RedisService;
import cn.ywenrou.shortlink.console.dao.entity.GroupDO;
import cn.ywenrou.shortlink.console.dao.entity.UserDO;
import cn.ywenrou.shortlink.console.dao.mapper.GroupMapper;
import cn.ywenrou.shortlink.console.dao.mapper.UserMapper;
import cn.ywenrou.shortlink.console.dto.req.UserLoginReqDTO;
import cn.ywenrou.shortlink.console.dto.req.UserPasswordUpdateReqDTO;
import cn.ywenrou.shortlink.console.dto.req.UserRegisterReqDTO;
import cn.ywenrou.shortlink.console.dto.req.UserUpdateReqDTO;
import cn.ywenrou.shortlink.console.dto.resp.UserInfoRespDTO;
import cn.ywenrou.shortlink.console.dto.resp.UserLoginRespDTO;
import cn.ywenrou.shortlink.console.service.UserService;
import cn.ywenrou.shortlink.console.common.utils.FileUploadUtils;
import cn.ywenrou.shortlink.common.security.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.ywenrou.shortlink.common.core.constant.CacheConstants.EXPIRATION;
import static cn.ywenrou.shortlink.common.core.constant.CacheConstants.USER_LOGIN_TOKEN_KEY;
import static cn.ywenrou.shortlink.common.core.enums.ErrorCodes.USER_NAME_EXIST_ERROR;
import static cn.ywenrou.shortlink.console.common.constant.RedisCacheConstants.LOCK_USER_REGISTER_KEY;
import static cn.ywenrou.shortlink.console.common.constant.ShortLinkConstants.DEFAULT_GROUP_NAME;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService  {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    public final RedisService redisService;
    private final GroupMapper groupMapper;
    private final FileUploadUtils fileUploadUtils;
    private int maxRetryCount = CacheConstants.PASSWORD_MAX_RETRY_COUNT;
    private Long lockTime = CacheConstants.PASSWORD_LOCK_TIME;

    @Override
    public boolean hasUsername(String username) {
        //使用布隆过滤器高效判断用户名是否存在
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterReqDTO requestParam) {
        if (hasUsername(requestParam.getUsername())) {
            throw new ClientException(USER_NAME_EXIST_ERROR);
        }
        //分布式锁,防止同一时间注册相同用户名
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUsername());

        lock.lock();
        try {
            // 双重检查锁定,第二次检查确保在获取锁的过程中没有其他线程成功注册相同用户名。
            if (hasUsername(requestParam.getUsername())) {
                throw new ClientException(USER_NAME_EXIST_ERROR);
            }
            UserDO userDO = BeanUtil.toBean(requestParam, UserDO.class);
            baseMapper.insert(userDO);
            userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
            GroupDO groupDO = GroupDO.builder()
                    .gid(IdUtil.simpleUUID())
                    .sortOrder(0)
                    .username(requestParam.getUsername())
                    .name(DEFAULT_GROUP_NAME)
                    .build();
            groupMapper.insert(groupDO);

        }catch (DuplicateKeyException ex) {
            throw new ClientException(USER_NAME_EXIST_ERROR);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        String username = SecurityUtils.getUsername();
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, username);
        baseMapper.update(BeanUtil.toBean(requestParam, UserDO.class), updateWrapper);
    }

    @Override
    public void updatePassword(UserPasswordUpdateReqDTO requestParam) {
        String username = SecurityUtils.getUsername();
        
        // 获取当前用户信息
        UserDO user = baseMapper.selectOne(Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username));
        
        if (user == null) {
            throw new ClientException("用户不存在");
        }
        
        // 验证旧密码
        if (!requestParam.getOldPassword().equals(user.getPassword())) {
            throw new ClientException("原密码不正确");
        }
        
        // 更新密码
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, username)
                .set(UserDO::getPassword, requestParam.getNewPassword());
        
        baseMapper.update(null, updateWrapper);
    }

    @Override
    public String updateAvatar(MultipartFile file) {
        String username = SecurityUtils.getUsername();

        // 上传新头像
        String newAvatarUrl = fileUploadUtils.uploadAvatar(file);

        // 更新用户头像
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, username)
                .set(UserDO::getAvatar, newAvatarUrl);
        
        baseMapper.update(null, updateWrapper);
        
        return newAvatarUrl;
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {

        String username = requestParam.getUsername();
        String password = requestParam.getPassword();
        String blackStr = Convert.toStr(redisService.getCacheObject(CacheConstants.SYS_LOGIN_BLACKIPLIST));
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr()))
        {
            throw new ServiceException("很遗憾，访问IP已被列入系统黑名单");
        }

        String passwordRetryLimitKey = CacheConstants.PWD_ERR_CNT_KEY + username;

        Integer retryCount = redisService.getCacheObject(passwordRetryLimitKey);

        if (retryCount == null)
        {
            retryCount = 0;
        }

        if (retryCount >= Integer.valueOf(maxRetryCount).intValue())
        {
            String errMsg = String.format("密码输入错误%s次，帐户锁定%s分钟", maxRetryCount, lockTime);
            throw new ServiceException(errMsg);
        }
        UserDO user = baseMapper.selectOne(Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username));

        if (user == null || !password.equals(user.getPassword()))
        {
            retryCount = retryCount + 1;
            redisService.setCacheObject(passwordRetryLimitKey, retryCount, lockTime, TimeUnit.MINUTES);
            throw new ServiceException("用户不存在/密码错误");
        }
        else
        {
            redisService.deleteObject(passwordRetryLimitKey);
        }
        UserInfoRespDTO loginUser = BeanUtil.toBean(user, UserInfoRespDTO.class);

        String uuid = IdUtil.fastUUID();
        long userId = loginUser.getId();
        String userName = loginUser.getUsername();
        String ip = IpUtils.getIpAddr();
        long loginTime = System.currentTimeMillis();
        long expireTime = loginTime + EXPIRATION;

        // redis存储用户信息
        redisService.setCacheObject(USER_LOGIN_TOKEN_KEY + uuid, loginUser, EXPIRATION, TimeUnit.MILLISECONDS);

        // Jwt存储信息
        Map<String, Object> claimsMap = new HashMap<String, Object>();
        claimsMap.put(SecurityConstants.USER_KEY, uuid);
        claimsMap.put(SecurityConstants.DETAILS_USER_ID, userId);
        claimsMap.put(SecurityConstants.DETAILS_USERNAME, userName);

        return new UserLoginRespDTO().setToken(JwtUtils.createToken(claimsMap))
                .setExpireTime(expireTime)
                .setLoginIp(ip)
                .setLoginTime(loginTime);
    }

    @Override
    public void logout() {
        redisService.deleteObject(USER_LOGIN_TOKEN_KEY + SecurityUtils.getUserKey());

    }

    @Override
    public void refreshToken() {
        redisService.expire(USER_LOGIN_TOKEN_KEY + SecurityUtils.getUserKey(), EXPIRATION, TimeUnit.MILLISECONDS);
    }

    @Override
    public UserInfoRespDTO getUserInfo() {
        String username = SecurityUtils.getUsername();
        UserDO userDO = baseMapper.selectOne(Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username));
        if (userDO==null) {
            throw new ClientException("用户不存在");
        }
        return BeanUtil.toBean(userDO, UserInfoRespDTO.class);
    }
}

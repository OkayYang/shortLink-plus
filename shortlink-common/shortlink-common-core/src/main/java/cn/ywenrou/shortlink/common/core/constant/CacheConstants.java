package cn.ywenrou.shortlink.common.core.constant;

/**
 * 缓存常量信息
 *
 * @author xuxiaoyang
 */
public class CacheConstants
{
    /**
     * 缓存有效期，默认720（分钟）
     */
    public final static long EXPIRATION = 720*60*1000;

    /**
     * 缓存刷新时间，默认120（分钟）
     */
    public final static long REFRESH_TIME = 120*60*1000;

    /**
     * 密码最大错误次数
     */
    public final static int PASSWORD_MAX_RETRY_COUNT = 5;

    /**
     * 密码锁定时间，默认10（分钟）
     */
    public final static long PASSWORD_LOCK_TIME = 10;

    /**
     * 用户注册分布式锁
     */
    public static final String LOCK_USER_REGISTER_KEY = "short-link:lock_user_register:";

    /**
     * 分组创建分布式锁
     */
    public static final String LOCK_GROUP_CREATE_KEY = "short-link:lock_group_create:";

    /**
     * 用户登录缓存标识
     */
    public static final String USER_LOGIN_TOKEN_KEY = "short-link:login_token:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "short-link:captcha_codes:";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "short-link:config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "short-link:dict:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "short-link:pwd_err_cnt:";

    /**
     * 登录IP黑名单 cache key
     */
    public static final String SYS_LOGIN_BLACKIPLIST = SYS_CONFIG_KEY + "login.blackIPList";
}


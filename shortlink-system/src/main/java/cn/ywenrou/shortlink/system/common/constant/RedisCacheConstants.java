package cn.ywenrou.shortlink.system.common.constant;

public class RedisCacheConstants {

    /**
     * 短链缓存标识
     */
    public static final String SHORT_LINK_KEY_CACHE = "short-link:link-cache:";

    /**
     * 短链接跳转分布式锁
     */
    public static final String SHORT_LINK_LOCK_REDIRECT = "short-link:lock_redirect:";

    /**
     * 短链接缓存有效期，单位：分钟
     */
    public static final long SHORT_LINK_EXPIRATION = 60*24*7;

    /**
     * 匿名用户key
     */
    public static final String ANONYMOUS_USER_KEY = "short-link:anonymous-user:";

    /**
     * 匿名用户Cookie标识
     */
    public static final String ANONYMOUS_USER_COOKIE_KEY = "short-link-anonymous-user";

    /**
     * 匿名用户每日生成限制
     */
    public static final int ANONYMOUS_USER_DAILY_LIMIT = 5;

    /**
     * 匿名用户历史记录key
     */
    public static final String ANONYMOUS_USER_HISTORY_KEY = "short-link:anonymous-history:";

    /**
     * 匿名用户每日生成次数key
     */
    public static final String ANONYMOUS_USER_DAILY_COUNT_KEY = "short-link:anonymous-count:";

    /**
     * 匿名用户分组
     */
    public static final String ANONYMOUS_USER_GROUP = "anonymous";

    /**
     * 匿名用户历史缓存保存时间
     */
    public static final int ANONYMOUS_HISTORY_DAYS = 7;

    /**
     * 匿名用户Cookie过期时间（秒）
     */
    public static final int ANONYMOUS_HISTORY_SECONDS = ANONYMOUS_HISTORY_DAYS * 24 * 60 * 60;

    /**
     * 匿名用户查询历史锁
     */
    public static final String ANONYMOUS_HISTORY_QUERY_LOCK = "short-link:anonymous-history-lock:";
}

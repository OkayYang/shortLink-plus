package cn.ywenrou.shortlink.common.security.utils;


import cn.ywenrou.shortlink.common.core.constant.SecurityConstants;
import cn.ywenrou.shortlink.common.core.constant.TokenConstants;
import cn.ywenrou.shortlink.common.core.context.SecurityContextHolder;
import cn.ywenrou.shortlink.common.core.utils.ServletUtils;
import cn.ywenrou.shortlink.common.core.utils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 权限获取工具类
 */
public class SecurityUtils
{
    /**
     * 获取用户ID
     */
    public static Long getUserId()
    {
        return SecurityContextHolder.getUserId();
    }

    /**
     * 获取用户名称
     */
    public static String getUsername()
    {
        return SecurityContextHolder.getUserName();
    }

    /**
     * 获取用户key
     */
    public static String getUserKey()
    {
        return SecurityContextHolder.getUserKey();
    }


    /**
     * 获取请求token
     */
    public static String getToken()
    {
        return getToken(ServletUtils.getRequest());
    }

    /**
     * 根据request获取请求token
     */
    public static String getToken(HttpServletRequest request)
    {
        // 从header获取token标识
        String token = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        return replaceTokenPrefix(token);
    }

    /**
     * 裁剪token前缀
     */
    public static String replaceTokenPrefix(String token)
    {
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX))
        {
            token = token.replaceFirst(TokenConstants.PREFIX, "");
        }
        return token;
    }
}


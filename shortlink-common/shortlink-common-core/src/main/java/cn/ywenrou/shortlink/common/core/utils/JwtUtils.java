package cn.ywenrou.shortlink.common.core.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.ywenrou.shortlink.common.core.constant.SecurityConstants;

import java.util.Map;

import static cn.ywenrou.shortlink.common.core.constant.SecurityConstants.DETAILS_USERNAME;
import static cn.ywenrou.shortlink.common.core.constant.SecurityConstants.DETAILS_USER_ID;


public class JwtUtils {

    private static final String SECRET = "nagedachangoffer";


    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    public static String createToken(Map<String, Object> claims)
    {
        return JWTUtil.createToken(claims, SECRET.getBytes());
    }

    /**
     * 根据令牌获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    public static String getUserId(String token)
    {
        JWT jwt = JWTUtil.parseToken(token);
        return jwt.getPayload(DETAILS_USER_ID).toString();
    }


    /**
     * 根据令牌获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public static String getUserName(String token)
    {
        JWT jwt = JWTUtil.parseToken(token);
        return jwt.getPayload(DETAILS_USERNAME).toString();
    }

    /**
     * 获取用户redis key
     * @param token
     * @return 用户redis key
     */

    public static String getUserKey(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return jwt.getPayload(SecurityConstants.USER_KEY).toString();
    }


    /**
     * 验证令牌
     * @param token
     * @return 令牌有效期
     */
    public static boolean verifyToken(String token) {

        return JWT.of(token).setKey(SECRET.getBytes()).verify();
    }


}

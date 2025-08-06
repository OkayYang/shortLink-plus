package cn.ywenrou.shortlink.common.security.interceptor;

import cn.ywenrou.shortlink.common.core.constant.SecurityConstants;
import cn.ywenrou.shortlink.common.core.utils.IpUtils;
import cn.ywenrou.shortlink.common.core.utils.ServletUtils;
import cn.ywenrou.shortlink.common.core.utils.StringUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * feign 请求拦截器
 *
 */
public class FeignRequestInterceptor implements RequestInterceptor
{
    // 域名相关的请求头
    private static final String[] HOST_HEADERS = {"Host", "X-Forwarded-Host", "X-Forwarded-Server", "X-Real-IP", "X-Original-Host"};
    
    @Override
    public void apply(RequestTemplate requestTemplate)
    {
        HttpServletRequest httpServletRequest = ServletUtils.getRequest();
        if (StringUtils.isNotNull(httpServletRequest))
        {
            Map<String, String> headers = ServletUtils.getHeaders(httpServletRequest);
            // 传递用户信息请求头，防止丢失
            String userId = headers.get(SecurityConstants.DETAILS_USER_ID);
            if (StringUtils.isNotEmpty(userId))
            {
                requestTemplate.header(SecurityConstants.DETAILS_USER_ID, userId);
            }
            String userKey = headers.get(SecurityConstants.USER_KEY);
            if (StringUtils.isNotEmpty(userKey))
            {
                requestTemplate.header(SecurityConstants.USER_KEY, userKey);
            }
            String userName = headers.get(SecurityConstants.DETAILS_USERNAME);
            if (StringUtils.isNotEmpty(userName))
            {
                requestTemplate.header(SecurityConstants.DETAILS_USERNAME, userName);
            }
            String authentication = headers.get(SecurityConstants.AUTHORIZATION_HEADER);
            if (StringUtils.isNotEmpty(authentication))
            {
                requestTemplate.header(SecurityConstants.AUTHORIZATION_HEADER, authentication);
            }
            
            // 传递域名相关的请求头
            for (String headerName : HOST_HEADERS) {
                String headerValue = headers.get(headerName);
                if (StringUtils.isNotEmpty(headerValue)) {
                    requestTemplate.header(headerName, headerValue);
                }
            }


            // 配置客户端IP
            requestTemplate.header("X-Forwarded-For", IpUtils.getIpAddr());
        }
    }
}
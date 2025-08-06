package cn.ywenrou.shortlink.common.security.config;

import cn.ywenrou.shortlink.common.security.interceptor.FeignRequestInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

/**
 * Feign 配置注册
 *
 **/
public class FeignAutoConfiguration
{
    @Bean
    public RequestInterceptor requestInterceptor()
    {
        return new FeignRequestInterceptor();
    }
}

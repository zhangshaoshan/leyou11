package com.leyou.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;


/**
 * 注册过滤器
 * */
@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {

    @Bean
    public AuthorityInterceptor getAuthorityInterceptor()
    {
        return new AuthorityInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 可添加多个
        registry.addInterceptor(getAuthorityInterceptor()).addPathPatterns("/**").excludePathPatterns(Arrays.asList("/check","/code","/register","/login","/error","/updatePassword"));//
    }
}

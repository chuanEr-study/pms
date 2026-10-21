package com.pms.personnel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：注册 OA 凭证拦截器，覆盖人员相关接口。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final OaAuthInterceptor oaAuthInterceptor;

    public WebConfig(OaAuthInterceptor oaAuthInterceptor) {
        this.oaAuthInterceptor = oaAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(oaAuthInterceptor).addPathPatterns("/api/personnel/**");
    }
}

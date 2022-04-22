package com.epoch.loan.workshop.api.web;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.Filter;

/**
 * @author : Duke
 * @packageName : com.epoch.pf.option.api.web
 * @className : WebConfig
 * @createTime : 2021/3/10 21:59
 * @description : 拦截器规则
 */
@SpringBootConfiguration
@ServletComponentScan
public class WebConfig extends WebMvcConfigurationSupport {

    /**
     * 注册贷超转发过滤器
     *
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean<Filter> forwardFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ForwardFilter());
        registration.addUrlPatterns("/*");
        registration.setName("forwardFilter");
        registration.setOrder(1);
        return registration;
    }

    /**
     * 拦截器Bean
     *
     * @return Interceptor
     */
    @Bean
    public Interceptor interceptor() {
        return new Interceptor();
    }

    /**
     * 拦截所有
     *
     * @param registry
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        // 可添加多个
        registry.addInterceptor(interceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

    /**
     * 静态资源
     *
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }
}

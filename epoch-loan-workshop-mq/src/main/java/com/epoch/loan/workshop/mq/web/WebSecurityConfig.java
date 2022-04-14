package com.epoch.loan.workshop.mq.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.web
 * @className : WebSecurityConfig
 * @createTime : 2021/11/16 20:00
 * @description : 订单审核通过
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().permitAll()
                .and().logout().permitAll();
    }
}

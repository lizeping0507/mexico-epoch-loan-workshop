package com.epoch.loan.workshop.api.web;

import com.epoch.loan.workshop.common.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;


/**
 * @author : Duke
 * @packageName : com.epoch.pf.option.api.web
 * @className : InitConfig
 * @createTime : 2021/3/10 21:59
 * @description : TODO 不知道干什么的
 */
@Configuration
public class InitConfig implements InitializingBean {

    @Resource(name = "jacksonObjectMapper")
    private ObjectMapper mapper;

    @Override
    public void afterPropertiesSet() throws Exception {
        JsonUtils.setObjectMapper(mapper);
    }
}
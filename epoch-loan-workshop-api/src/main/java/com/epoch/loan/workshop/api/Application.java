package com.epoch.loan.workshop.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.epoch.loan.workshop.common.config.StartConfig;
import com.epoch.loan.workshop.common.constant.DynamicRequest;
import com.epoch.loan.workshop.common.dao.mysql.LoanDynamicRequestDao;
import com.epoch.loan.workshop.common.entity.mysql.LoanDynamicRequestEntity;
import com.epoch.loan.workshop.common.mq.log.LogMQManager;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.zookeeper.ZookeeperClient;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq
 * @className : Application
 * @createTime : 2021/11/3 15:00
 * @description : Api模块启动类
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.epoch.loan.workshop"})
@DubboComponentScan(basePackages = {"com.epoch.loan.workshop.api.controller"})
@MapperScan(basePackages = "com.epoch.loan.workshop.common.dao.mysql")
@EnableElasticsearchRepositories(basePackages = "com.epoch.loan.workshop.common.dao.elastic")
@EnableAspectJAutoProxy(exposeProxy = true)
public class Application {
    /**
     * 动态接口配置
     */
    @Autowired
    public LoanDynamicRequestDao dynamicRequestDao;

    /**
     * 日志队列
     */
    @Autowired
    private LogMQManager logMQManager;

    /**
     * 启动类
     *
     * @param args
     */
    public static void main(String[] args) {
        // 初始化配置
        StartConfig.initConfig();

        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void startJob() throws Exception {
        // 初始化动态地址配置
        loadDynamicRequestConfig();

        // 日志的队列初始化
        logMQManager.init();
    }

    private void loadDynamicRequestConfig() {
        List<LoanDynamicRequestEntity> dynamicRequests = dynamicRequestDao.findAll();
        dynamicRequests.forEach(dynamicRequest -> {
            DynamicRequest.URL_MAPPING_CACHE.put(dynamicRequest.getMappingUrl(), dynamicRequest.getUrl());
            try {
                DynamicRequest.REQUEST_MAPPING_MAPPING_CACHE.put(
                        dynamicRequest.getMappingUrl(),
                        JSON.parseObject(dynamicRequest.getMappingRequestParams(), new TypeReference<Map<String, String>>() {
                        }));
                DynamicRequest.RESPONSE_MAPPING_MAPPING_CACHE.put(
                        dynamicRequest.getMappingUrl(),
                        JSON.parseObject(dynamicRequest.getMappingResponseParams(), new TypeReference<Map<String, String>>() {
                        }));
                DynamicRequest.RESPONSE_MAPPING_VIRTUAL_PARAMS_CACHE.put(
                        dynamicRequest.getMappingUrl(),
                        JSON.parseObject(dynamicRequest.getMappingVirtualParams(), new TypeReference<List<String>>() {
                        }));
            } catch (Exception e) {
                LogUtil.sysError("[Application loadDynamicRequestConfig]", e);
            }
        });
    }
}

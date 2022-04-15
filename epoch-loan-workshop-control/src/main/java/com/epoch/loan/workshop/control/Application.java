package com.epoch.loan.workshop.control;

import com.epoch.loan.workshop.common.config.StartConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.control
 * @className : Application
 * @createTime : 2021/11/3 15:00
 * @description : Account模块启动类
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.epoch.loan.workshop"})
@MapperScan(basePackages = "com.epoch.loan.workshop.common.dao.mysql")
@EnableDubbo(scanBasePackages = {"com.epoch.loan.workshop.control.service", "com.epoch.loan.workshop.common.service"})
@EnableElasticsearchRepositories(basePackages = "com.epoch.loan.workshop.common.dao.elastic")
@EnableAspectJAutoProxy(exposeProxy = true)
public class Application {

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
}

package com.epoch.loan.workshop.order;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.account
 * @className : Application
 * @createTime : 2021/11/3 15:00
 * @description : Account模块启动类
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.epoch.loan.workshop"})
@DubboComponentScan(basePackages = {"com.epoch.loan.workshop.order"})
@MapperScan(basePackages = "com.epoch.loan.workshop.common.dao")
@EnableDubbo(scanBasePackages = {"com.epoch.loan.workshop.order.service"})
@EnableAsync
@EnableAspectJAutoProxy(exposeProxy = true)
public class Application {
    /**
     * 启动类
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 启动后调用
     *
     * @throws Exception
     */
    @PostConstruct
    public void startJob() throws Exception {
    }
}

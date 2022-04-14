package com.epoch.loan.workshop.spring.admin;


import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.spring.admin
 * @className : Application
 * @createTime : 2021/11/3 15:00
 * @description : Api模块启动类
 */
@Configuration
@EnableAutoConfiguration
@EnableAdminServer
public class Application {

    /**
     * 启动类
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

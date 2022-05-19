package com.epoch.loan.workshop.timing;

import com.epoch.loan.workshop.common.config.StartConfig;
import com.epoch.loan.workshop.common.mq.collection.CollectionMQManager;
import com.epoch.loan.workshop.common.mq.log.LogMQManager;
import com.epoch.loan.workshop.common.mq.order.OrderMQManager;
import com.epoch.loan.workshop.common.mq.remittance.RemittanceMQManager;
import com.epoch.loan.workshop.common.mq.repayment.RepaymentMQManager;
import com.epoch.loan.workshop.timing.util.QuartzUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.annotation.PostConstruct;

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
@MapperScan(basePackages = "com.epoch.loan.workshop.common.dao.mysql")
@EnableElasticsearchRepositories(basePackages = "com.epoch.loan.workshop.common.dao.elastic")
@EnableAspectJAutoProxy(exposeProxy = true)
public class Application {
    /**
     * collection
     */
    @Autowired
    public CollectionMQManager collectionMQManager;
    /**
     * 订单队列
     */
    @Autowired
    public OrderMQManager orderMQManager;
    /**
     * 日志队列
     */
    @Autowired
    public LogMQManager logMQManager;

    /**
     * 还款队列
     */
    @Autowired
    public RepaymentMQManager repaymentMQManager;
    /**
     * 放款队列
     */
    @Autowired
    protected RemittanceMQManager remittanceMQManager;
    /**
     * 定时任务
     */
    @Autowired
    private QuartzUtil quartzUtil;

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

    /**
     * 启动后调用
     *
     * @throws Exception
     */
    @PostConstruct
    public void startJob() throws Exception {
        remittanceMQManager.init();
        repaymentMQManager.init();
        logMQManager.init();
        orderMQManager.init();
        collectionMQManager.init();
        quartzUtil.startJob();
    }
}

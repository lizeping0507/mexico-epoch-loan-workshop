package com.epoch.loan.workshop.mq;

import com.epoch.loan.workshop.common.config.StartConfig;
import com.epoch.loan.workshop.common.mq.collection.CollectionMQManager;
import com.epoch.loan.workshop.common.mq.order.OrderMQManager;
import com.epoch.loan.workshop.common.mq.remittance.RemittanceMQManager;
import com.epoch.loan.workshop.common.mq.repayment.RepaymentMQManager;
import com.epoch.loan.workshop.common.zookeeper.ZookeeperClient;
import com.epoch.loan.workshop.mq.collection.Collection;
import com.epoch.loan.workshop.mq.log.AccessLogStorage;
import com.epoch.loan.workshop.mq.order.*;
import com.epoch.loan.workshop.mq.order.screen.RiskModelV1;
import com.epoch.loan.workshop.mq.remittance.Distribution;
import com.epoch.loan.workshop.mq.remittance.payment.panda.PandaPay;
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
     * 还款队列生产
     */
    @Autowired
    public RepaymentMQManager repaymentMQManager;

    /**
     * 还款队列生产
     */
    @Autowired
    public com.epoch.loan.workshop.mq.repayment.Distribution distributionRepayment;

    /**
     * Zookeeper工具类
     */
    @Autowired
    public ZookeeperClient zookeeperClient;

    /**
     * 订单队列
     */
    @Autowired
    private OrderMQManager orderMQManager;

    /**
     * 风控V3策略队列
     */
    @Autowired
    private RiskModelV1 riskModelV1;

    /**
     * 审批通过
     */
    @Autowired
    private OrderExaminePass orderExaminePass;

    /**
     * 订单在途
     */
    @Autowired
    private OrderWay orderWay;

    /**
     * 订单完成
     */
    @Autowired
    private OrderComplete orderComplete;

    /**
     * 订单逾期
     */
    @Autowired
    private OrderDue orderDue;

    /**
     * 汇款分配队列
     */
    @Autowired
    private RemittanceMQManager remittanceMQManagerProduct;

    /**
     * 汇款分配队列
     */
    @Autowired
    private Distribution distributionRemittance;

    /**
     * 订单放款
     */
    @Autowired
    private OrderRemittance orderRemittance;
    /**
     * 响应日志
     */
    @Autowired
    private AccessLogStorage accessLogStorage;
    /**
     * 响应日志
     */
    @Autowired
    private PandaPay pandaPayPayment;
    /**
     * 响应日志
     */
    @Autowired
    private com.epoch.loan.workshop.mq.repayment.panda.PandaPay pandaPayRePayment;

    /**
     * 推送催收、提还队列
     */
    @Autowired
    public Collection collection;

    /**
     * 催收、提还队列管理
     */
    @Autowired
    private CollectionMQManager collectionMQManager;


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
        zookeeperClient.init();
        orderMQManager.init();
        repaymentMQManager.init();
        remittanceMQManagerProduct.init();
        collectionMQManager.init();
        orderComplete.start();
        orderDue.start();
        orderRemittance.start();
        riskModelV1.start();
        orderExaminePass.start();
        orderWay.start();
        distributionRemittance.start();
        accessLogStorage.start();
        distributionRepayment.start();
        pandaPayRePayment.start();
        pandaPayPayment.start();
        collection.start();
    }
}

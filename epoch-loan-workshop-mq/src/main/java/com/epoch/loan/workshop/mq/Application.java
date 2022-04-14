package com.epoch.loan.workshop.mq;

import com.epoch.loan.workshop.common.config.StartConfig;
import com.epoch.loan.workshop.common.mq.order.OrderMQManager;
import com.epoch.loan.workshop.common.mq.remittance.RemittanceMQManager;
import com.epoch.loan.workshop.mq.order.*;
import com.epoch.loan.workshop.mq.order.screen.*;
import com.epoch.loan.workshop.mq.remittance.Distribution;
import com.epoch.loan.workshop.mq.remittance.payment.ac.AcPay;
import com.epoch.loan.workshop.mq.remittance.payment.fast.FastPay;
import com.epoch.loan.workshop.mq.remittance.payment.glob.GlobPay;
import com.epoch.loan.workshop.mq.remittance.payment.hr.HrPay;
import com.epoch.loan.workshop.mq.remittance.payment.in.InPay;
import com.epoch.loan.workshop.mq.remittance.payment.incash.InCashPay;
import com.epoch.loan.workshop.mq.remittance.payment.incash.InCashXjdPay;
import com.epoch.loan.workshop.mq.remittance.payment.ocean.OceanPay;
import com.epoch.loan.workshop.mq.remittance.payment.qe.QePay;
import com.epoch.loan.workshop.mq.remittance.payment.sunflower.SunFlowerPay;
import com.epoch.loan.workshop.mq.remittance.payment.trust.TrustPay;
import com.epoch.loan.workshop.mq.remittance.payment.yeah.YeahPay;
import com.epoch.loan.workshop.mq.remittance.payment.yeah.YeahPay1;
import com.epoch.loan.workshop.mq.remittance.payment.yeah.YeahPay2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

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
@MapperScan(basePackages = "com.epoch.loan.workshop.common.dao")
@EnableAsync
public class Application {
    /**
     * 订单队列
     */
    @Autowired
    private OrderMQManager orderMQManager;

    /**
     * 风控V3策略队列
     */
    @Autowired
    private RiskModelV3 riskModelV3;

    /**
     * 风控V2策略队列
     */
    @Autowired
    private RiskModelV2 riskModelV2;

    /**
     * 公有云数据推送
     */
    @Autowired
    private PublicCloudInfoPush publicCloudInfoPush;

    /**
     * 放款推送
     */
    @Autowired
    private PublicCloudPayPush publicCloudPayPush;

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
     * 订单完成
     */
    @Autowired
    private PublicCloudPaySuccessPush publicCloudPaySuccessPush;

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
    private Distribution distribution;
    /**
     * yeahPay放款队列
     */
    @Autowired
    private YeahPay yeahPay;
    /**
     * yeahPay2放款队列
     */
    @Autowired
    private YeahPay2 yeahPay2;
    /**
     * yeahPa1放款队列
     */
    @Autowired
    private YeahPay1 yeahPay1;
    /**
     * fastPay放款队列
     */
    @Autowired
    private FastPay fastPay;
    /**
     * inPay放款队列
     */
    @Autowired
    private InPay inPay;
    /**
     * subFlowerPay放款队列
     */
    @Autowired
    private SunFlowerPay sunFlowerPay;
    /**
     * oceanPay放款队列
     */
    @Autowired
    private OceanPay oceanPay;
    /**
     * acPay放款队列
     */
    @Autowired
    private AcPay acPay;
    /**
     * IncashPay放款队列
     */
    @Autowired
    private InCashPay inCashPay;
    /**
     * IncashXjdPay放款队列
     */
    @Autowired
    private InCashXjdPay incashXjdPay;
    /**
     * TrustPay放款队列
     */
    @Autowired
    private TrustPay trustPay;
    /**
     * QePay放款队列
     */
    @Autowired
    private QePay qePay;
    /**
     * HrPay放款队列
     */
    @Autowired
    private HrPay hrPay;
    /**
     * GlobPay放款队列
     */
    @Autowired
    private GlobPay globPay;
    /**
     * 订单放款
     */
    @Autowired
    private OrderRemittance orderRemittance;

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
        orderMQManager.init();
        remittanceMQManagerProduct.init();
        orderComplete.start();
        orderDue.start();
        publicCloudPayPush.start();
        publicCloudInfoPush.start();
        publicCloudPaySuccessPush.start();
        orderRemittance.start();
        riskModelV3.start();
        orderExaminePass.start();
        orderWay.start();
        riskModelV2.start();
        distribution.start();
        yeahPay.start();
        fastPay.start();
        inPay.start();
        yeahPay2.start();
        yeahPay1.start();
        sunFlowerPay.start();
        oceanPay.start();
        acPay.start();
        inCashPay.start();
        incashXjdPay.start();
        trustPay.start();
        qePay.start();
        hrPay.start();
        globPay.start();
    }
}

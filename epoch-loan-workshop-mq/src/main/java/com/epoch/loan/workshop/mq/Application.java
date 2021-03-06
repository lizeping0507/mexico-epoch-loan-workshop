package com.epoch.loan.workshop.mq;

import com.epoch.loan.workshop.common.config.StartConfig;
import com.epoch.loan.workshop.common.mq.order.OrderMQManager;
import com.epoch.loan.workshop.common.mq.remittance.RemittanceMQManager;
import com.epoch.loan.workshop.common.mq.repayment.RepaymentMQManager;
import com.epoch.loan.workshop.mq.log.AccessLogStorage;
import com.epoch.loan.workshop.mq.order.*;
import com.epoch.loan.workshop.mq.order.screen.*;
import com.epoch.loan.workshop.mq.remittance.DistributionRemittance;
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
import com.epoch.loan.workshop.mq.repayment.DistributionRepayment;
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
 * @description : Api???????????????
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.epoch.loan.workshop"})
@MapperScan(basePackages = "com.epoch.loan.workshop.common.dao.mysql")
@EnableElasticsearchRepositories(basePackages = "com.epoch.loan.workshop.common.dao.elastic")
@EnableAspectJAutoProxy(exposeProxy = true)
public class Application {
    /**
     * ????????????
     */
    @Autowired
    private OrderMQManager orderMQManager;

    /**
     * ??????V3????????????
     */
    @Autowired
    private RiskModelV3 riskModelV3;

    /**
     * ??????V2????????????
     */
    @Autowired
    private RiskModelV2 riskModelV2;

    /**
     * ?????????????????????
     */
    @Autowired
    private PublicCloudInfoPush publicCloudInfoPush;

    /**
     * ????????????
     */
    @Autowired
    private PublicCloudPayPush publicCloudPayPush;

    /**
     * ????????????
     */
    @Autowired
    private OrderExaminePass orderExaminePass;

    /**
     * ????????????
     */
    @Autowired
    private OrderWay orderWay;

    /**
     * ????????????
     */
    @Autowired
    private OrderComplete orderComplete;

    /**
     * ????????????
     */
    @Autowired
    private PublicCloudPaySuccessPush publicCloudPaySuccessPush;

    /**
     * ????????????
     */
    @Autowired
    private OrderDue orderDue;

    /**
     * ??????????????????
     */
    @Autowired
    private RemittanceMQManager remittanceMQManagerProduct;

    /**
     * ??????????????????
     */
    @Autowired
    private DistributionRemittance distributionRemittance;

    /**
     * yeahPay????????????
     */
    @Autowired
    private YeahPay yeahPay;

    /**
     * yeahPay2????????????
     */
    @Autowired
    private YeahPay2 yeahPay2;

    /**
     * yeahPa1????????????
     */
    @Autowired
    private YeahPay1 yeahPay1;

    /**
     * fastPay????????????
     */
    @Autowired
    private FastPay fastPay;

    /**
     * inPay????????????
     */
    @Autowired
    private InPay inPay;

    /**
     * subFlowerPay????????????
     */
    @Autowired
    private SunFlowerPay sunFlowerPay;

    /**
     * oceanPay????????????
     */
    @Autowired
    private OceanPay oceanPay;

    /**
     * acPay????????????
     */
    @Autowired
    private AcPay acPay;

    /**
     * IncashPay????????????
     */
    @Autowired
    private InCashPay inCashPay;
    /**
     * IncashXjdPay????????????
     */
    @Autowired
    private InCashXjdPay incashXjdPay;
    /**
     * TrustPay????????????
     */
    @Autowired
    private TrustPay trustPay;
    /**
     * QePay????????????
     */
    @Autowired
    private QePay qePay;
    /**
     * HrPay????????????
     */
    @Autowired
    private HrPay hrPay;
    /**
     * GlobPay????????????
     */
    @Autowired
    private GlobPay globPay;

    /**
     * yeahPay????????????
     */
    @Autowired
    private com.epoch.loan.workshop.mq.repayment.yeah.YeahPay1 repaymentYeahPay1;

    /**
     * ????????????
     */
    @Autowired
    private OrderRemittance orderRemittance;

    /**
     * ????????????
     */
    @Autowired
    private AccessLogStorage accessLogStorage;
    /**
     * ??????????????????
     */
    @Autowired
    public RepaymentMQManager repaymentMQManager;
    /**
     * ??????????????????
     */
    @Autowired
    public DistributionRepayment distributionRepayment;

    /**
     * ?????????
     *
     * @param args
     */
    public static void main(String[] args) {
        // ???????????????
        StartConfig.initConfig();

        SpringApplication.run(Application.class, args);

    }

    /**
     * ???????????????
     *
     * @throws Exception
     */
    @PostConstruct
    public void startJob() throws Exception {
        orderMQManager.init();
        repaymentMQManager.init();
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
        distributionRemittance.start();
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
        accessLogStorage.start();
        globPay.start();
        repaymentYeahPay1.start();
        distributionRepayment.start();
    }
}

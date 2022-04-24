package com.epoch.loan.workshop.control.service;

import com.epoch.loan.workshop.common.config.PlatformConfig;
import com.epoch.loan.workshop.common.config.RiskConfig;
import com.epoch.loan.workshop.common.dao.elastic.OcrLivingDetectionLogElasticDao;
import com.epoch.loan.workshop.common.dao.mysql.*;
import com.epoch.loan.workshop.common.redis.RedisClient;
import com.epoch.loan.workshop.common.sms.SMSManager;
import com.epoch.loan.workshop.common.zookeeper.ZookeeperClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.control.service;
 * @className : BaseService
 * @createTime : 2022/3/22 14:28
 * @description : control模块业务基类
 */
public class BaseService {
    /**
     * Zookeeper工具类
     */
    @Autowired
    public ZookeeperClient zookeeperClient;

    /**
     * 短信
     */
    @Autowired
    public SMSManager smsManager;

    /**
     * 动态接口配置
     */
    @Autowired
    public LoanDynamicRequestDao dynamicRequestDao;

    /**
     * 贷超相关配置
     */
    @Autowired
    public PlatformConfig platformConfig;

    /**
     * 三方OCR相关配置
     */
    @Autowired
    public LoanOcrProviderConfigDao loanOcrProviderConfigDao;

    /**
     * Redis工具类
     */
    @Autowired
    public RedisClient redisClient;

    /**
     * advance日志
     */
    @Autowired
    public OcrLivingDetectionLogElasticDao ocrLivingDetectionLogElasticDao;

    /**
     * 订单
     */
    @Autowired
    public LoanOrderDao loanOrderDao;

    /**
     * 放款账户
     */
    @Autowired
    public LoanRemittanceAccountDao loanRemittanceAccountDao;

    /**
     * 风控配置
     */
    @Autowired
    public RiskConfig riskConfig;

    /**
     * 放款银行
     */
    @Autowired
    public LoanRemittanceBankDao loanRemittanceBankDao;

    /**
     * 用户
     */
    @Autowired
    public LoanUserDao loanUserDao;

    /**
     * 用户
     */
    @Autowired
    public LoanOrderBillDao loanOrderBillDao;

    /**
     * 产品
     */
    @Autowired
    public LoanProductDao loanProductDao;

    /**
     * 订单模型
     */
    @Autowired
    public LoanOrderModelDao loanOrderModelDao;

    /**
     * 订单审核
     */
    @Autowired
    public LoanOrderExamineDao loanOrderExamineDao;

    /**
     * 渠道
     */
    @Autowired
    public PlatformChannelDao platformChannelDao;

    /**
     * 渠道
     */
    @Autowired
    public PlatformUserAuthDao platformUserAuthDao;
}

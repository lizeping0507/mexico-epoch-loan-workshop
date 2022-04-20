package com.epoch.loan.workshop.account.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.authentication.TokenManager;
import com.epoch.loan.workshop.common.config.PlatformConfig;
import com.epoch.loan.workshop.common.dao.elastic.OcrLivingDetectionLogElasticDao;
import com.epoch.loan.workshop.common.dao.mysql.*;
import com.epoch.loan.workshop.common.entity.mysql.LoanRemittancePaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.remittance.RemittanceMQManager;
import com.epoch.loan.workshop.common.mq.remittance.params.RemittanceParams;
import com.epoch.loan.workshop.common.mq.repayment.RepaymentMQManager;
import com.epoch.loan.workshop.common.redis.RedisClient;
import com.epoch.loan.workshop.common.sms.SMSManager;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.account.service.payment;
 * @className : BasePaymentService
 * @createTime : 2022/2/10 16:42
 * @description : 支付基类
 */
public class BaseService {
    /**
     * Redis工具类
     */
    @Autowired
    public RedisClient redisClient;
    /**
     * 短信
     */
    @Autowired
    public SMSManager smsManager;
    /**
     * token工具
     */
    @Autowired
    public TokenManager tokenManager;
    /**
     * 新用户表
     */
    @Autowired
    public LoanUserDao loanUserDao;
    /**
     * 新用户详细表
     */
    @Autowired
    public LoanUserInfoDao loanUserInfoDao;
    /**
     * 还款分配队列
     */
    @Autowired
    public RepaymentMQManager repaymentMQManager;
    /**
     * 用户
     */
    @Autowired
    public PlatformUserDao platformUserDao;
    /**
     * 用户
     */
    @Autowired
    public PlatformOrderDao platformOrderDao;
    /**
     * 银行卡
     */
    @Autowired
    public PlatformUserBankCardDao platformUserBankCardDao;
    /**
     * 基础信息
     */
    @Autowired
    public PlatformUserBasicInfoDao platformUserBasicInfoDao;
    /**
     * 新订单表
     */
    @Autowired
    public LoanOrderDao loanOrderDao;
    /**
     * 还款配置
     */
    @Autowired
    public LoanProductRepaymentConfigDao loanProductRepaymentConfigDao;
    /**
     * 还款渠道权重配置
     */
    @Autowired
    public LoanRepaymentDistributionDao loanRepaymentDistributionDao;
    /**
     * 订单账单
     */
    @Autowired
    public LoanOrderBillDao loanOrderBillDao;
    /**
     * 放款流水
     */
    @Autowired
    public LoanRepaymentPaymentRecordDao loanRepaymentPaymentRecordDao;

    /**
     * 汇款分配队列
     */
    @Autowired
    public RemittanceMQManager remittanceMQManagerProduct;
    /**
     * 汇款分配队列
     */
    @Autowired
    public LoanPaymentDao loanPaymentDao;
    /**
     * 订单放款记录
     */
    @Autowired
    LoanRemittanceOrderRecordDao orderRecordDao;
    /**
     * 贷超相关配置
     */
    @Autowired
    PlatformConfig platformConfig;
    /**
     * 支付放款记录
     */
    @Autowired
    LoanRemittancePaymentRecordDao paymentRecordDao;

    /**
     * 三方OCR相关配置
     */
    @Autowired
    public LoanOcrProviderConfigDao loanOcrProviderConfigDao;

    /**
     * advance日志
     */
    @Autowired
    public OcrLivingDetectionLogElasticDao ocrLivingDetectionLogElasticDao;

    /**
     * 发送队列
     *
     * @param orderId 订单号
     * @param tag     标签
     * @return boolean
     */
    public boolean sendToQueue(String orderId, String tag) {
        // 查询付款详情 队列参数
        LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.getByOrderId(orderId);

        // 校验
        if (ObjectUtils.isEmpty(paymentRecord) || StringUtils.isEmpty(paymentRecord.getQueueParam())) {
            return Boolean.FALSE;
        }

        // 入队列
        RemittanceParams remittanceParams = JSONObject.parseObject(paymentRecord.getQueueParam(), RemittanceParams.class);
        try {
            remittanceMQManagerProduct.sendMessage(remittanceParams, tag);
        } catch (Exception e) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
}

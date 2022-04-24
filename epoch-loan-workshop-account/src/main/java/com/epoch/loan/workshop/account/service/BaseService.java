package com.epoch.loan.workshop.account.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.authentication.TokenManager;
import com.epoch.loan.workshop.common.config.PlatformConfig;
import com.epoch.loan.workshop.common.dao.elastic.OcrLivingDetectionLogElasticDao;
import com.epoch.loan.workshop.common.dao.mysql.*;
import com.epoch.loan.workshop.common.entity.mysql.LoanRemittancePaymentRecordEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanUserEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanUserInfoEntity;
import com.epoch.loan.workshop.common.mq.remittance.RemittanceMQManager;
import com.epoch.loan.workshop.common.mq.remittance.params.RemittanceParams;
import com.epoch.loan.workshop.common.mq.repayment.RepaymentMQManager;
import com.epoch.loan.workshop.common.params.User;
import com.epoch.loan.workshop.common.redis.RedisClient;
import com.epoch.loan.workshop.common.sms.SMSManager;
import com.epoch.loan.workshop.common.util.LogUtil;
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
//    @Autowired
//    public PlatformUserBankCardDao platformUserBankCardDao;
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

    /**
     * 更新用户信息缓存
     *
     * @return boolean
     */
    public void updateUserCache(String userId) {
        User user = new User();
        LoanUserEntity userEntity = loanUserDao.findById(userId);
        LoanUserInfoEntity userInfoEntity = loanUserInfoDao.findUserInfoById(userId);

        user.setId(userEntity.getId());
        user.setAndroidId(userEntity.getAndroidId());
        user.setChannelId(userEntity.getChannelId());
        user.setGaId(userEntity.getGaId());
        user.setImei(userEntity.getImei());
        user.setPlatform(userEntity.getPlatform());
        user.setLoginName(userEntity.getLoginName());
        user.setPassword(userEntity.getPassword());
        user.setAppName(userEntity.getAppName());
        user.setAppVersion(userEntity.getAppVersion());
        user.setUserInfoId(userInfoEntity.getId());
        user.setMobile(userInfoEntity.getMobile());
        user.setGps(userInfoEntity.getGps());
        user.setGpsAddress(userInfoEntity.getGpsAddress());
        user.setRegisterGps(userInfoEntity.getRegisterGps());
        user.setRegisterAddress(userInfoEntity.getRegisterAddress());
        user.setIp(userInfoEntity.getIp());
        user.setIpAddress(userInfoEntity.getIpAddress());
        user.setContacts(userInfoEntity.getContacts());
        user.setMonthlyIncome(userInfoEntity.getMonthlyIncome());
        user.setPayPeriod(userInfoEntity.getPayPeriod());
        user.setOccupation(userInfoEntity.getOccupation());
        user.setPayMethod(userInfoEntity.getPayMethod());
        user.setEmail(userInfoEntity.getEmail());
        user.setEducation(userInfoEntity.getEducation());
        user.setMarital(userInfoEntity.getMarital());
        user.setChildrenNumber(userInfoEntity.getChildrenNumber());
        user.setLoanPurpose(userInfoEntity.getLoanPurpose());
        user.setLiveType(userInfoEntity.getLiveType());
        user.setPapersAddress(userInfoEntity.getPapersAddress());
        user.setPapersFatherName(userInfoEntity.getPapersFatherName());
        user.setPapersName(userInfoEntity.getPapersName());
        user.setPapersMotherName(userInfoEntity.getPapersMotherName());
        user.setPapersFullName(userInfoEntity.getPapersFullName());
        user.setPapersId(userInfoEntity.getPapersId());
        user.setPapersVoterId(userInfoEntity.getPapersVoterId());
        user.setPapersGender(userInfoEntity.getPapersGender());
        user.setPapersAge(userInfoEntity.getPapersAge());
        user.setPapersDateOfBirth(userInfoEntity.getPapersDateOfBirth());
        user.setPostalCode(userInfoEntity.getPostalCode());
        user.setRfc(userInfoEntity.getRfc());
        user.setCustomFatherName(userInfoEntity.getCustomFatherName());
        user.setCustomName(userInfoEntity.getCustomName());
        user.setCustomMotherName(userInfoEntity.getCustomMotherName());
        user.setCustomFullName(userInfoEntity.getCustomFullName());
        user.setUserFileBucketName(userInfoEntity.getUserFileBucketName());

        LogUtil.sysInfo("用户信息缓存更新: {}", JSONObject.toJSONString(user));
        tokenManager.updateUserCache(user);
    }
}

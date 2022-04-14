package com.epoch.loan.workshop.account.service;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.dao.LoanPaymentDao;
import com.epoch.loan.workshop.common.dao.LoanRemittanceOrderRecordDao;
import com.epoch.loan.workshop.common.dao.LoanRemittancePaymentRecordDao;
import com.epoch.loan.workshop.common.entity.LoanRemittancePaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.remittance.RemittanceMQManager;
import com.epoch.loan.workshop.common.mq.remittance.params.RemittanceParams;
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
     * 订单放款记录
     */
    @Autowired
    LoanRemittanceOrderRecordDao orderRecordDao;

    /**
     * 支付放款记录
     */
    @Autowired
    LoanRemittancePaymentRecordDao paymentRecordDao;

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
     * 发送队列
     *
     * @param orderId 订单号
     * @param tag 标签
     * @return boolean
     */
    public boolean sendToQueue(String orderId, String tag) {
        // 查询付款详情 队列参数
        LoanRemittancePaymentRecordEntity paymentRecord = paymentRecordDao.getByOrderId(orderId);

        // 校验
        if (ObjectUtils.isEmpty(paymentRecord) || StringUtils.isEmpty(paymentRecord.getQueueParam())) {
            LogUtil.sysInfo("付款详情记录不存在:{}", orderId);
            return Boolean.FALSE;
        }

        // 入队列
        RemittanceParams remittanceParams = JSONObject.parseObject(paymentRecord.getQueueParam(), RemittanceParams.class);
        try {
            remittanceMQManagerProduct.sendMessage(remittanceParams, tag);
        } catch (Exception e) {
            LogUtil.sysInfo("入队列失败:{}", orderId);
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
}

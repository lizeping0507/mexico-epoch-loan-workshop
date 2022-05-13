package com.epoch.loan.workshop.mq.remittance.payment;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.constant.LoanRemittancePaymentRecordStatus;
import com.epoch.loan.workshop.common.dao.mysql.*;
import com.epoch.loan.workshop.common.entity.mysql.LoanRemittanceOrderRecordEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRemittancePaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.remittance.params.DistributionRemittanceParams;
import com.epoch.loan.workshop.common.mq.remittance.params.RemittanceParams;
import com.epoch.loan.workshop.common.util.ObjectIdUtil;
import com.epoch.loan.workshop.mq.remittance.BaseRemittanceMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.remittance.payment
 * @className : BaseRemittancePaymentMQ
 * @createTime : 2021/12/16 11:01
 * @description : 付款队列基类
 */
@RefreshScope
@Component
public abstract class BaseRemittancePaymentMQListener extends BaseRemittanceMQListener {

    /**
     * 支付分配
     */
    @Autowired
    public LoanRemittanceDistributionDao loanRemittanceDistributionDao;

    /**
     * 策略配置
     */
    @Autowired
    public LoanProductRemittanceConfigDao loanProductRemittanceConfigDao;

    /**
     * 支付记录
     */
    @Autowired
    public LoanRemittanceOrderRecordDao loanRemittanceOrderRecordDao;

    /**
     * 支付记录详情
     */
    @Autowired
    public LoanRemittancePaymentRecordDao loanRemittancePaymentRecordDao;

    /**
     * 支付渠道
     */
    @Autowired
    public LoanPaymentDao loanPaymentDao;

    /**
     * 重回放款队列
     *
     * @param params 放款队列参数
     * @param tag    标签
     * @throws Exception E
     */
    public void retryRemittance(RemittanceParams params, String tag) throws Exception {

        // 更新队列参数到库
        updateLoanRemittancePaymentRecordQueueParam(params);

        // 重入放款队列
        remittanceMQManager.sendMessage(params, tag, 60);
    }

    /**
     * 放款队列消息重回分配队列
     *
     * @param remittanceParams 放款队列消息
     * @param orderRecord      订单记录
     * @throws Exception E
     */
    protected void remittanceRetryDistribution(RemittanceParams remittanceParams, LoanRemittanceOrderRecordEntity orderRecord) throws Exception {
        DistributionRemittanceParams params = new DistributionRemittanceParams();
        params.setId(orderRecord.getId());
        List<String> paymentFilter = remittanceParams.getPaymentFilter() == null ? new ArrayList<>() : remittanceParams.getPaymentFilter();
        paymentFilter.add(orderRecord.getPaymentId());
        params.setPaymentFilter(paymentFilter);
        params.setGroupName(remittanceParams.getGroupName());
        retryDistribution(params, remittanceMQManager.getDistributionSubExpression());
    }

    /**
     * 更新支付记录详情 队列参数
     *
     * @param remittanceParams 队列参数
     */
    public void updateLoanRemittancePaymentRecordQueueParam(RemittanceParams remittanceParams) {
        String queueParam = JSONObject.toJSONString(remittanceParams);
        loanRemittancePaymentRecordDao.updateQueueParam(remittanceParams.getId(), queueParam, new Date());
    }

    /**
     * 更新支付记录详情状态
     *
     * @param id     id
     * @param status 状态
     */
    public void updateLoanRemittancePaymentRecordStatus(String id, Integer status) {
        loanRemittancePaymentRecordDao.updateStatus(id, status, new Date());
    }

    /**
     * 更新支付记录详情放款请求和响应参数
     *
     * @param id       id
     * @param request  请求内容
     * @param response 想要内容
     */
    public void updateLoanRemittancePaymentRecordLog(String id, String request, String response) {
        loanRemittancePaymentRecordDao.updateRequestAndResponse(id, request, response, new Date());
    }

    /**
     * 更新支付记录详情查询请求和响应参数
     *
     * @param id       id
     * @param request  请求内容
     * @param response 响应内容
     */
    public void updateLoanRemittancePaymentRecordSearchLog(String id, String request, String response) {
        loanRemittancePaymentRecordDao.updateSearchRequestAndSearchResponse(id, request, response, new Date());
    }

    /**
     * 更新放款详情记录 使用的配置标记
     *
     * @param id        id
     * @param configTag 配置标签
     */
    public void updateRemittancePaymentRecordConfigTag(String id, String configTag) {
        loanRemittancePaymentRecordDao.updateRemittancePaymentRecordConfigTag(id, configTag, new Date());
    }

    /**
     * 创建放款详情记录
     *
     * @param orderRecordId 订单支付记录Id
     * @param paymentId     支付渠道
     * @param configTag     配置标记
     * @return
     */
    protected String insertRemittancePaymentRecord(String orderRecordId, String paymentId, String configTag) {
        LoanRemittancePaymentRecordEntity loanRemittancePaymentRecordEntity = new LoanRemittancePaymentRecordEntity();
        String paymentLogId = ObjectIdUtil.getObjectId();
        loanRemittancePaymentRecordEntity.setId(paymentLogId);
        loanRemittancePaymentRecordEntity.setStatus(LoanRemittancePaymentRecordStatus.CREATE);
        loanRemittancePaymentRecordEntity.setPaymentId(paymentId);
        loanRemittancePaymentRecordEntity.setRemittanceOrderRecordId(orderRecordId);
        loanRemittancePaymentRecordEntity.setConfigTag(configTag);
        loanRemittancePaymentRecordEntity.setCreateTime(new Date());
        loanRemittancePaymentRecordEntity.setUpdateTime(new Date());
        loanRemittancePaymentRecordDao.insert(loanRemittancePaymentRecordEntity);
        return paymentLogId;
    }
}

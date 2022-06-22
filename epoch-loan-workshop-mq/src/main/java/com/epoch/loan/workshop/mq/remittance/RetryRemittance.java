package com.epoch.loan.workshop.mq.remittance;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.common.dao.mysql.LoanOrderDao;
import com.epoch.loan.workshop.common.dao.mysql.LoanPaymentDao;
import com.epoch.loan.workshop.common.dao.mysql.LoanRemittanceOrderRecordDao;
import com.epoch.loan.workshop.common.dao.mysql.LoanRemittancePaymentRecordDao;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRemittanceOrderRecordEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRemittancePaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.remittance.RemittanceMQManager;
import com.epoch.loan.workshop.common.mq.remittance.params.DistributionRemittanceParams;
import com.epoch.loan.workshop.common.mq.remittance.params.RemittanceParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 进行中放款重入队列
 */
@Component
public class RetryRemittance {
    @Autowired
    LoanRemittanceOrderRecordDao loanRemittanceOrderRecordDao;
    @Autowired
    LoanRemittancePaymentRecordDao loanRemittancePaymentRecordDao;
    @Autowired
    LoanOrderDao loanOrderDao;
    @Autowired
    LoanPaymentDao loanPaymentDao;
    @Autowired
    RemittanceMQManager remittanceMqManager;

    public void retry() {

        Set<String> idSet = new HashSet<>();

        // 查询进行放款记录
        List<LoanRemittanceOrderRecordEntity> list = loanRemittanceOrderRecordDao.findByStatus(20);
        for (LoanRemittanceOrderRecordEntity loanRemittanceOrderRecordEntity : list) {
            idSet.add(loanRemittanceOrderRecordEntity.getId());
        }

        int[] status = new int[]{10, 20};
        List<LoanRemittancePaymentRecordEntity> list1 = loanRemittancePaymentRecordDao.findByStatus(status);
        for (LoanRemittancePaymentRecordEntity entity : list1) {
            String remittanceOrderRecordId = entity.getRemittanceOrderRecordId();
            if (idSet.contains(remittanceOrderRecordId)) {
                continue;
            }
            idSet.add(remittanceOrderRecordId);
            LoanRemittanceOrderRecordEntity orderRecord = loanRemittanceOrderRecordDao.getById(remittanceOrderRecordId);
            if (ObjectUtils.isNotEmpty(orderRecord)){
                list.add(orderRecord);
            }
        }

        LogUtil.sysInfo("RetryRemittance start : {}", list.size());

        for (LoanRemittanceOrderRecordEntity orderRecordEntity : list) {
            try {
                String processRemittancePaymentRecordId = orderRecordEntity.getProcessRemittancePaymentRecordId();
                LoanRemittancePaymentRecordEntity paymentRecord = loanRemittancePaymentRecordDao.findById(processRemittancePaymentRecordId);
                if (ObjectUtils.isEmpty(paymentRecord)) {
                    continue;
                }

                String orderId = orderRecordEntity.getOrderId();
                LoanOrderEntity order = loanOrderDao.findOrder(orderId);
                if (ObjectUtils.isEmpty(order)) {
                    continue;
                }

                String paymentId = paymentRecord.getPaymentId();
                LoanPaymentEntity loanPayment = loanPaymentDao.getById(paymentId);
                if (ObjectUtils.isEmpty(loanPayment)) {
                    continue;
                }

                RemittanceParams remittanceParams = new RemittanceParams();
                if (StringUtil.isNotBlank(paymentRecord.getQueueParam())) {
                    remittanceParams = JSONObject.parseObject(paymentRecord.getQueueParam(), RemittanceParams.class);
                } else {
                    remittanceParams.setId(processRemittancePaymentRecordId);
                    remittanceParams.setGroupName(order.getRemittanceDistributionGroup());
                }

                String name = loanPayment.getName();
                remittanceMqManager.sendMessage(remittanceParams, name);
            } catch (Exception e) {
                LogUtil.sysError("[RetryRemittance " + orderRecordEntity.getId() + "]", e);
            }
        }

        LogUtil.sysInfo("RetryRemittance end");


        List<LoanRemittanceOrderRecordEntity> createdOrderRecordList = loanRemittanceOrderRecordDao.findByStatus(10);
        LogUtil.sysInfo("RetryCreatedRemittance start : {}", createdOrderRecordList.size());
        for (LoanRemittanceOrderRecordEntity loanRemittanceOrderRecordEntity : createdOrderRecordList) {
            try {
                // 订单
                String orderId = loanRemittanceOrderRecordEntity.getOrderId();
                LoanOrderEntity order = loanOrderDao.findOrder(orderId);

                DistributionRemittanceParams params = new DistributionRemittanceParams();
                params.setGroupName(order.getRemittanceDistributionGroup());
                params.setId(loanRemittanceOrderRecordEntity.getId());
                params.setPaymentFilter(new ArrayList<>());
                remittanceMqManager.sendMessage(params, remittanceMqManager.getDistributionSubExpression());
            } catch (Exception e) {
                LogUtil.sysError("[RetryRemittance " + loanRemittanceOrderRecordEntity.getId() + "]", e);
            }
        }
        LogUtil.sysInfo("RetryCreatedRemittance start : {}", list.size());
    }

}

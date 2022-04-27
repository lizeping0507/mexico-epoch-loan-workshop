package com.epoch.loan.workshop.account.repayment;

import com.epoch.loan.workshop.common.dao.mysql.LoanRepaymentPaymentRecordDao;
import com.epoch.loan.workshop.common.entity.mysql.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRepaymentPaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.repayment.RepaymentMQManager;
import com.epoch.loan.workshop.common.params.params.request.RepaymentParams;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * 渠道代收
 *
 * @author Shangkunfeng
 * @packageName com.epoch.loan.workshop.common.repayment
 * @className : BaseRepaymentUtil
 * @createTime : 2021/12/18 17:49
 * @description : 渠道发起放款基类
 */
public abstract class BaseRepayment {
    /**
     * 放款流水
     */
    @Autowired
    protected LoanRepaymentPaymentRecordDao loanRepaymentPaymentRecordDao;
    /**
     * 放款队列操作
     */
    @Autowired
    protected RepaymentMQManager repaymentMQManager;

    /**
     * 发起代收
     * 如果发起成功就返回支付页面地址
     * 如果发起失败就返空, 调用者会做判断
     *
     * @param loanRepaymentPaymentRecordEntity 支付详情
     * @param payment                          支付渠道
     * @param params
     * @return 付款页面
     */
    public abstract String startRepayment(LoanRepaymentPaymentRecordEntity loanRepaymentPaymentRecordEntity, LoanPaymentEntity payment, RepaymentParams params);

    /**
     * 存储业务所需id
     *
     * @param id         id
     * @param businessId 业务Id
     */
    protected void updatePamentRecordBussinesId(String id, String businessId) {
        loanRepaymentPaymentRecordDao.updateBussinesId(id, businessId, new Date());
    }

    /**
     * 更新代收记录详情请求参数和响应参数
     *
     * @param id       记录id
     * @param request  请求参数
     * @param response 响应参数
     */
    protected void updatePaymentRecordRequestAndResponse(String id, String request, String response) {
        loanRepaymentPaymentRecordDao.updateRequestAndResponse(id, request, response, new Date());
    }

    /**
     * 更新代收记录详情状态
     *
     * @param id     记录id
     * @param status 状态
     */
    protected void updatePaymentRecordStatus(String id, Integer status) {
        loanRepaymentPaymentRecordDao.updateStatus(id, status, new Date());
    }

}

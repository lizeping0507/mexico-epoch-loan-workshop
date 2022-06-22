package com.epoch.loan.workshop.timing.task;

import com.epoch.loan.workshop.common.constant.LoanRepaymentPaymentRecordStatus;
import com.epoch.loan.workshop.common.entity.mysql.LoanPaymentEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanRepaymentPaymentRecordEntity;
import com.epoch.loan.workshop.common.mq.repayment.params.RepaymentParams;
import com.epoch.loan.workshop.common.util.DateUtil;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.timing.task
 * @className : OrderRepaymentRetryTask
 * @createTime : 2022/4/7 18:49
 * @description : 还款订单结果查询重试
 */
@DisallowConcurrentExecution
@Component
public class OrderRepaymentRetryTask extends BaseTask implements Job {

    /**
     * 任务
     *
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 查询24小时内 30分钟外 支付状态为进行中的记录
        Date now = new Date();
        Date startTime = DateUtil.addHour(now,-24);
        Date endTime = DateUtil.addMinute(now, -30);
        List<LoanRepaymentPaymentRecordEntity> paymentRecords = loanRepaymentPaymentRecordDao.findByStatusAndTime(LoanRepaymentPaymentRecordStatus.PROCESS, startTime, endTime);

        // 重入队列
        paymentRecords.forEach(paymentRecord -> {
            try {
                // 查询支付渠道
                String paymentId = paymentRecord.getPaymentId();
                LoanPaymentEntity payment = loanPaymentDao.getById(paymentId);

                // 重入队列
                RepaymentParams repaymentParams = new RepaymentParams();
                repaymentParams.setId(paymentRecord.getId());
                repaymentMQManager.sendMessage(repaymentParams, payment.getName());
            } catch (Exception e) {
                LogUtil.sysError("[OrderRepaymentRetryTask]", e);
                e.printStackTrace();
            }
        });


    }
}

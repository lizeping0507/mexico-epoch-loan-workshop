package com.epoch.loan.workshop.timing.task;

import com.epoch.loan.workshop.common.constant.OrderBillStatus;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderBillEntity;
import com.epoch.loan.workshop.common.mq.repayment.params.DistributionRepaymentParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.timing.task
 * @className : OrderRepaymentDistributionTask
 * @createTime : 2022/4/7 18:49
 * @description : 订单还款分配任务
 */
@DisallowConcurrentExecution
@Component
public class OrderRepaymentDistributionTask extends BaseTask implements Job {

    /**
     * 任务
     *
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 查询在途订单
        List<LoanOrderBillEntity> loanOrderBillEntityListWay = loanOrderBillDao.findOrderBillByStatus(OrderBillStatus.WAY);
        LogUtil.sysInfo("Way:" + loanOrderBillEntityListWay.size());
        loanOrderBillEntityListWay.parallelStream().forEach(loanOrderBillEntity -> {
            try {
                // 发送还款策略组分配队列计算还款策略
                DistributionRepaymentParams distributionRepaymentParams = new DistributionRepaymentParams();
                distributionRepaymentParams.setOrderId(loanOrderBillEntity.getOrderId());
                distributionRepaymentParams.setOrderBillId(loanOrderBillEntity.getId());
                repaymentMQManager.sendMessage(distributionRepaymentParams, repaymentMQManager.getDistributionSubExpression());
            } catch (Exception e) {
                LogUtil.sysError("[OrderRepaymentDistributionTask]", e);
            }
        });


        // 查询逾期订单
        List<LoanOrderBillEntity> loanOrderBillEntityListDue = loanOrderBillDao.findOrderBillByStatus(OrderBillStatus.DUE);
        LogUtil.sysInfo("Way:" + loanOrderBillEntityListDue.size());
        loanOrderBillEntityListDue.parallelStream().forEach(loanOrderBillEntity -> {
            try {
                // 发送还款策略组分配队列计算还款策略
                DistributionRepaymentParams distributionRepaymentParams = new DistributionRepaymentParams();
                distributionRepaymentParams.setOrderId(loanOrderBillEntity.getOrderId());
                distributionRepaymentParams.setOrderBillId(loanOrderBillEntity.getId());
                repaymentMQManager.sendMessage(distributionRepaymentParams, repaymentMQManager.getDistributionSubExpression());
            } catch (Exception e) {
                LogUtil.sysError("[OrderRepaymentDistributionTask]", e);
            }
        });
    }
}

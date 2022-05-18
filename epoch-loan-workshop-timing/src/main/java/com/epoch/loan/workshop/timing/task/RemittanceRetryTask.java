package com.epoch.loan.workshop.timing.task;

import com.epoch.loan.workshop.common.constant.LoanRemittanceOrderRecordStatus;
import com.epoch.loan.workshop.common.entity.mysql.LoanRemittanceOrderRecordEntity;
import com.epoch.loan.workshop.common.mq.remittance.params.DistributionRemittanceParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : package com.epoch.loan.workshop.timing.task;
 * @className : RemittanceRetryTask
 * @createTime : 2021/12/15 15:09
 * @description : 付款订单重试
 */
@DisallowConcurrentExecution
@Component
public class RemittanceRetryTask extends BaseTask implements Job {

    /**
     * 方法主体
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        // 查询放款失败的订单支付记录
        List<LoanRemittanceOrderRecordEntity> failedOrderRecords = loanRemittanceOrderRecordDao.findByStatus(LoanRemittanceOrderRecordStatus.FAILED);

        // 遍历处理
        failedOrderRecords.forEach(failedOrderRecord -> {
            // 重入分配队列
            try {
                // 查询订单使用的支付策略组
                String groupName = loanOrderDao.findRemittanceDistributionGroupById(failedOrderRecord.getOrderId());

                // 封装分配队列参数
                DistributionRemittanceParams params = new DistributionRemittanceParams();
                params.setGroupName(groupName);
                params.setId(failedOrderRecord.getId());
                remittanceMQManager.sendMessage(params, remittanceMQManager.getDistributionSubExpression());
            } catch (Exception exception) {
                LogUtil.sysError("[RemittanceRetryTask]", exception);
            }
        });
    }
}

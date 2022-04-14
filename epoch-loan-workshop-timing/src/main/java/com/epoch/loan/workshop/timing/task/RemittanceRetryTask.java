package com.epoch.loan.workshop.timing.task;

import com.epoch.loan.workshop.common.constant.LoanRemittanceOrderRecordStatus;
import com.epoch.loan.workshop.common.entity.LoanRemittanceOrderRecordEntity;
import com.epoch.loan.workshop.common.mq.remittance.params.DistributionParams;
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
        List<LoanRemittanceOrderRecordEntity> failedOrderRecords
                = loanRemittanceOrderRecordDao.findByStatus(LoanRemittanceOrderRecordStatus.FAILED);

        // 遍历处理
        failedOrderRecords.forEach(failedOrderRecord -> {
            // 查询订单使用的支付策略组
            String groupName = loanOrderDao.findRemittanceDistributionGroupById(failedOrderRecord.getOrderId());

            // 封装分配队列参数
            DistributionParams params = new DistributionParams();
            params.setGroupName(groupName);
            params.setId(failedOrderRecord.getId());

            // 重入分配队列
            try {
                remittanceMqManagerProduct.sendMessage(params, remittanceMqManagerProduct.getDistributionSubExpression());
                LogUtil.sysInfo("[RemittanceRetryTask]重入分配:{}",failedOrderRecord.getId());
            } catch (Exception exception) {
                LogUtil.sysError("[RemittanceRetryTask]", exception);
            }
        });
    }
}

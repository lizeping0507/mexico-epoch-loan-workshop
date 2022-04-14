package com.epoch.loan.workshop.timing.task;

import com.epoch.loan.workshop.common.constant.OrderExamineStatus;
import com.epoch.loan.workshop.common.entity.LoanOrderExamineEntity;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.util.DateUtil;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author : Shangkunfeng
 * @packageName : package com.epoch.loan.workshop.timing.task;
 * @className : RiskModelV3ToQueueTask
 * @createTime : 2021/12/15 15:09
 * @description : 等待状态订单重入队列
 */
@DisallowConcurrentExecution
@Component
public class RiskModelV3ToQueueTask extends BaseTask implements Job {

    /**
     * 方法主体
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        // 查询 模型为风控决策V3（RiskModelV3） 状态为等待（20） 更新时间为今天前
        Date zero = DateUtil.getStartForDay();
        List<LoanOrderExamineEntity> loanOrderExamines = loanOrderExamineDao.findByModelNameAndStatusBeforTime("RiskModelV3", OrderExamineStatus.WAIT, zero);

        // 模型名称列表
        List<String> modelNames = loanOrderModelDao.findNamesByGroup("MASK-INTERNAL-LOAN");

        // 订单放入队列
        loanOrderExamines.forEach(loanOrderExamineEntity -> {
            try {
                OrderParams orderParams = new OrderParams();
                orderParams.setOrderId(loanOrderExamineEntity.getOrderId());
                orderParams.setModelList(modelNames);
                orderParams.setGroupName("MASK-INTERNAL-LOAN");
                orderMQManager.sendMessage(orderParams, modelNames.get(0));
            } catch (Exception exception) {
                LogUtil.sysError("[RiskModelV3ToQueueTask]", exception);
            }
        });

    }
}

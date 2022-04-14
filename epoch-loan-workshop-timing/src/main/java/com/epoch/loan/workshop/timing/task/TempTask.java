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
 * @packageName : com.epoch.loan.workshop.timing.task;
 * @className : TempTask
 * @createTime : 2022/4/2 17:17
 * @description : 临时处理
 */
@DisallowConcurrentExecution
@Component
public class TempTask extends BaseTask implements Job {

    /**
     * 方法主体
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        Date date = new Date();
        List<LoanOrderExamineEntity> riskModelV3 = loanOrderExamineDao.findByModelNameAndStatusBeforTime("RiskModelV3",21, date);
        List<LoanOrderExamineEntity> riskModelV2 = loanOrderExamineDao.findByModelNameAndStatusBeforTime("RiskModelV2",21, date);

        LogUtil.sysInfo(riskModelV3.size() + "  ====== " + riskModelV2.size());


        // 模型名称列表
        List<String> modelNames = loanOrderModelDao.findNamesByGroup("MASK-INTERNAL-LOAN");

        // 订单放入队列
        riskModelV3.forEach(loanOrderExamineEntity -> {
            try {
                OrderParams orderParams = new OrderParams();
                orderParams.setOrderId(loanOrderExamineEntity.getOrderId());
                orderParams.setModelList(modelNames);
                orderParams.setGroupName("MASK-INTERNAL-LOAN");
                orderMQManager.sendMessage(orderParams, modelNames.get(0));
                LogUtil.sysInfo("MASK-INTERNAL-LOAN   " + loanOrderExamineEntity.getOrderId());
            } catch (Exception exception) {
                LogUtil.sysError("[RiskModelV3ToQueueTask]", exception);
            }
        });

        // 模型名称列表
        List<String> modelNames1 = loanOrderModelDao.findNamesByGroup("SHOP-INTERNAL-LOAN");
        // 订单放入队列
        riskModelV2.forEach(loanOrderExamineEntity -> {
            try {
                OrderParams orderParams = new OrderParams();
                orderParams.setOrderId(loanOrderExamineEntity.getOrderId());
                orderParams.setModelList(modelNames1);
                orderParams.setGroupName("SHOP-INTERNAL-LOAN");
                orderMQManager.sendMessage(orderParams, modelNames1.get(0));
                LogUtil.sysInfo("SHOP-INTERNAL-LOAN   "+ loanOrderExamineEntity.getOrderId());
            } catch (Exception exception) {
                LogUtil.sysError("[RiskModelV2ToQueueTask]", exception);
            }
        });

    }
}

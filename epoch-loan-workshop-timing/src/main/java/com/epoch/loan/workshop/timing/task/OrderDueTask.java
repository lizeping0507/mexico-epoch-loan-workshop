package com.epoch.loan.workshop.timing.task;

import com.epoch.loan.workshop.common.constant.OrderBillStatus;
import com.epoch.loan.workshop.common.constant.RedisKeyField;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderBillEntity;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.util.LogUtil;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.timing.task
 * @className : OrderDueTask
 * @createTime : 2022/4/7 18:48
 * @description : 订单逾期定时任务
 */
@DisallowConcurrentExecution
@Component
public class OrderDueTask extends BaseTask implements Job {

    /**
     * 任务
     *
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 查询所有在途已逾期的订单
        List<LoanOrderBillEntity> loanOrderBillEntityListWay = loanOrderBillDao.findOrderBillByStatusAndRepaymentTime(OrderBillStatus.WAY, new Date());

        // 查询所有已经逾期的订单
        List<LoanOrderBillEntity> loanOrderBillEntityListDue = loanOrderBillDao.findOrderBillByStatus(OrderBillStatus.DUE);

        // 需要计算的逾期订单
        List<LoanOrderBillEntity> loanOrderBillEntityList = new ArrayList<>();
        loanOrderBillEntityList.addAll(loanOrderBillEntityListWay);
        loanOrderBillEntityList.addAll(loanOrderBillEntityListDue);

        // 必须将所有订单处理完
        while (true) {
            // 集合为空退出循环
            if (CollectionUtils.isEmpty(loanOrderBillEntityList)) {
                break;
            }

            // 循环插入队列计算逾期
            for (LoanOrderBillEntity loanOrderBillEntity : loanOrderBillEntityList) {
                try {
                    // 罚息大于或等于本金
                    if (loanOrderBillEntity.getPunishmentAmount() >= loanOrderBillEntity.getPrincipalAmount()){
                        // 删除数据
                        loanOrderBillEntityList.remove(loanOrderBillEntity);
                        continue;
                    }

                    // 订单ID
                    String orderId = loanOrderBillEntity.getOrderId();

                    // 订单账单ID
                    String orderBillId = loanOrderBillEntity.getId();

                    OrderParams params = new OrderParams();
                    params.setGroupName("SYSTEM");
                    params.setOrderId(orderId);
                    params.setOrderBillId(orderBillId);
                    orderMQManager.sendMessage(params, orderMQManager.getOrderDueSubExpression());

                    // 增加逾期计算标识
                    redisClient.set(RedisKeyField.ORDER_BILL_DUE_LOCK + orderId, orderBillId);

                    // 删除数据
                    loanOrderBillEntityList.remove(loanOrderBillEntity);
                } catch (Exception e) {
                    LogUtil.sysError("[OrderDueTask]", e);
                }
            }
        }
    }
}

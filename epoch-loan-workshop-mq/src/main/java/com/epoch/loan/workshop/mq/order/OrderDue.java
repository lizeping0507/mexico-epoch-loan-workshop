package com.epoch.loan.workshop.mq.order;

import com.epoch.loan.workshop.common.constant.CollectionField;
import com.epoch.loan.workshop.common.constant.OrderBillStatus;
import com.epoch.loan.workshop.common.constant.OrderStatus;
import com.epoch.loan.workshop.common.constant.RedisKeyField;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderBillEntity;
import com.epoch.loan.workshop.common.entity.mysql.LoanOrderEntity;
import com.epoch.loan.workshop.common.mq.order.params.OrderParams;
import com.epoch.loan.workshop.common.util.DateUtil;
import com.epoch.loan.workshop.common.util.LogUtil;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.mq.order
 * @className : OrderDue
 * @createTime : 2021/11/16 18:11
 * @description : 订单逾期
 */
@RefreshScope
@Component
@Data
public class OrderDue extends BaseOrderMQListener implements MessageListenerConcurrently {

    /**
     * 消息监听器
     */
    private MessageListenerConcurrently messageListener = this;

    /**
     * 消费任务
     *
     * @param msgs    消息列表
     * @param context 消息轨迹对象
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        // 循环处理消息
        for (Message msg : msgs) {
            // 消息对象
            OrderParams orderParams = null;

            try {
                // 获取消息对象
                orderParams = getMessage(msg);
                if (ObjectUtils.isEmpty(orderParams)) {
                    continue;
                }

                // 队列拦截
                if (intercept("SYSTEM", subExpression())) {
                    // 等待重试
                    retry(orderParams, subExpression());
                    continue;
                }

                // 订单id
                String orderId = orderParams.getOrderId();

                // 订单账单id
                String orderBillId = orderParams.getOrderBillId();

                // 查询订单ID
                LoanOrderEntity loanOrderEntity = loanOrderDao.findOrder(orderId);
                if (ObjectUtils.isEmpty(loanOrderEntity)) {
                    continue;
                }

                // 根据订单账单id查询账单
                LoanOrderBillEntity loanOrderBillEntity = loanOrderBillDao.findOrderBill(orderBillId);
                if (ObjectUtils.isEmpty(loanOrderBillEntity)) {
                    continue;
                }

                // 判断是否逾期
                if (loanOrderBillEntity.getRepaymentTime().getTime() > System.currentTimeMillis()) {
                    continue;
                }

                // 计算逾期天数
                int dueDay = DateUtil.getIntervalDays(DateUtil.getDefault(), DateUtil.DateToString(loanOrderBillEntity.getRepaymentTime(), "yyyy-MM-dd"), "yyyy-MM-dd");

                // 本期应还还款本金
                double principalAmount = loanOrderBillEntity.getPrincipalAmount();

                // 利息金额
                double interestAmount = loanOrderBillEntity.getInterestAmount();

                // 罚息利率
                double penaltyInterest = loanOrderEntity.getPenaltyInterest();

                // 计算罚息金额
                double punishmentAmount = principalAmount * ((penaltyInterest / 100) * dueDay);
                if (punishmentAmount > principalAmount) {
                    punishmentAmount = principalAmount;
                }

                // 应还款金额
                double repaymentAmount = principalAmount + interestAmount + punishmentAmount;

                // 更新减免金额
                loanOrderBillDao.updateOrderBillReductionAmount(orderBillId, 0.00, new Date());

                // 更新罚息金额
                loanOrderBillDao.updateOrderBillPunishmentAmount(orderBillId, punishmentAmount, new Date());

                // 更新还款金额
                loanOrderBillDao.updateOrderBillRepaymentAmount(orderBillId, repaymentAmount, new Date());

                // 计算预计还款金额更新预计还款金额
                double amount = loanOrderBillDao.sumOrderRepaymentAmount(orderId);
                loanOrderDao.updateOrderEstimatedRepaymentAmount(orderId, amount, new Date());

                // 更新订单账单状态为逾期
                updateOrderBillStatus(orderBillId, OrderBillStatus.DUE);

                // 更新订单状态为逾期
                updateOrderStatus(orderId, OrderStatus.DUE);

                // 推送催收
                sendCollection(orderId, CollectionField.EVENT_DUE);

                // 删除计算标识
                redisClient.del(RedisKeyField.ORDER_BILL_DUE_LOCK + orderId);
            } catch (Exception e) {
                try {
                    // 异常,重试
                    retry(orderParams, subExpression());
                } catch (Exception exception) {
                    LogUtil.sysError("[OrderDue]", exception);
                }

                LogUtil.sysError("[OrderDue]", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

}
